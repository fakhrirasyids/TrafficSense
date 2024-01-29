package com.lazycode.trafficsense.ui.carpool

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityCarpoolBinding
import com.lazycode.trafficsense.ui.adapter.CarpoolPagerAdapter
import com.lazycode.trafficsense.ui.carpool.addcarpool.AddCarpoolActivity
import com.lazycode.trafficsense.ui.carpool.carpoolhistory.CarpoolHistoryActivity
import com.lazycode.trafficsense.ui.carpool.vehicle.VehicleActivity
import com.lazycode.trafficsense.ui.carpool.verify.BottomSheetVerify
import com.lazycode.trafficsense.ui.carpool.verify.BottomSheetVerifySuccess
import com.lazycode.trafficsense.ui.carpool.verify.BottomSheetVerifyWaiting
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import com.lazycode.trafficsense.utils.UserPreferences
import com.lazycode.trafficsense.utils.UserPreferences.Companion.preferenceDefaultValue
import org.koin.android.viewmodel.ext.android.viewModel


class CarpoolActivity : AppCompatActivity() {
    lateinit var binding: ActivityCarpoolBinding
    val carpoolViewModel: CarpoolViewModel by viewModel()

    private lateinit var loadingDialog: AlertDialog

    private val addCarpoolLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            carpoolViewModel.getCarpoolingData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarpoolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        setProfileInfo()
        setViewPager()

        setSwipeRefresh()
        setListeners()
    }

    private fun setProfileInfo() {
        carpoolViewModel.apply {
            getProfilePic().observe(this@CarpoolActivity) {
                if (it != UserPreferences.preferenceDefaultValue) {
                    Glide.with(this@CarpoolActivity)
                        .load(it)
                        .into(binding.ivAvatar)
                }
            }

            getFullname().observe(this@CarpoolActivity) {
                if (it != UserPreferences.preferenceDefaultValue) {
                    binding.tvUsername.text = StringBuilder("Hi, $it")
                }
            }

            getPhone().observe(this@CarpoolActivity) {
                if (it == UserPreferences.preferenceDefaultValue) {
                    binding.tvPhoneNumber.isVisible = false
                } else {
                    binding.tvPhoneNumber.isVisible = true
                    binding.tvPhoneNumber.text = it
                }
            }
        }
    }

    private fun setViewPager() {
        val carpoolPagerAdapter = CarpoolPagerAdapter(this)
        binding.viewPager.adapter = carpoolPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(tabTitles[position])
        }.attach()
    }


    private fun setListeners() {
        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener { finish() }
                inflateMenu(R.menu.main_menu)
                setOnMenuItemClickListener {
                    if (it.itemId == R.id.menu_settings) {
                        startActivity(
                            Intent(
                                this@CarpoolActivity,
                                ProfileSettingsActivity::class.java
                            )
                        )
                    }
                    true
                }
            }

            btnHistory.setOnClickListener {
                val iHistory = Intent(this@CarpoolActivity, CarpoolHistoryActivity::class.java)
                startActivity(iHistory)
            }

            btnExpandableLayout.setOnClickListener {
                val flagContentLayout =
                    if (layoutExpandableContent.isVisible) View.GONE else View.VISIBLE
                val initialHeight =
                    expandableContent.height

                if (layoutExpandableContent.isVisible) {
                    iconExpandable.setImageDrawable(getDrawable(R.drawable.ic_next_navigate))
                    val va = ValueAnimator.ofInt(initialHeight, 100)
                    va.duration = 300
                    va.addUpdateListener { animation ->
                        val value = animation.animatedValue as Int
                        expandableContent.layoutParams.height = value
                        expandableContent.requestLayout()
                    }
                    va.start()
                    layoutExpandableContent.visibility = flagContentLayout
                } else {
                    layoutExpandableContent.visibility = flagContentLayout

                    expandableContent.measure(
                        View.MeasureSpec.makeMeasureSpec(
                            expandableContent.width,
                            View.MeasureSpec.EXACTLY
                        ),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    val targetHeight = expandableContent.measuredHeight

                    iconExpandable.setImageDrawable(getDrawable(R.drawable.ic_down))
                    val va = ValueAnimator.ofInt(initialHeight, targetHeight)
                    va.duration = 300
                    va.addUpdateListener { animation ->
                        val value = animation.animatedValue as Int
                        expandableContent.layoutParams.height = value
                        expandableContent.requestLayout()
                    }
                    va.start()
                }

            }

            btnAddCarpool.setOnClickListener {
                if (isAddValid()) {
                    carpoolViewModel.getDocuments().observe(this@CarpoolActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {
                                var flagVerified = true
                                val userDocuments = result.data.payload

                                Log.i("KLOOT", "setListeners: ${result.data}")

                                if (userDocuments!!.isEmpty()) {
                                    loadingDialog.dismiss()
                                    val bottomSheetVerify = BottomSheetVerify()
                                    bottomSheetVerify.isCancelable = false
                                    bottomSheetVerify.show(
                                        supportFragmentManager,
                                        "CarpoolActivity"
                                    )
                                } else {
                                    for (document in userDocuments) {
                                        if (document?.isVerified.toString().toInt() == 0) {
                                            flagVerified = false
                                            break
                                        }
                                    }

                                    loadingDialog.dismiss()
                                    if (flagVerified) {
                                        val iAddCarpool = Intent(
                                            this@CarpoolActivity,
                                            AddCarpoolActivity::class.java
                                        )
                                        addCarpoolLauncher.launch(iAddCarpool)
                                    } else {
                                        val bottomSheetVerifyWaiting = BottomSheetVerifyWaiting()
                                        bottomSheetVerifyWaiting.show(
                                            supportFragmentManager,
                                            "CarpoolActivity"
                                        )
                                    }
                                }
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()
                                alertDialogMessage(
                                    this@CarpoolActivity,
                                    "Gagal mendapatkan info dokumen!",
                                    "Error"
                                )
                            }
                        }
                    }
                }
            }

            btnVerifyCarpool.setOnClickListener {
                carpoolViewModel.getDocuments().observe(this@CarpoolActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            loadingDialog.show()
                        }

                        is Result.Success -> {
                            var flagVerified = true
                            val userDocuments = result.data.payload

                            Log.i("KLOOT", "setListeners: ${result.data}")

                            if (userDocuments!!.isEmpty()) {
                                loadingDialog.dismiss()
                                val bottomSheetVerify = BottomSheetVerify()
                                bottomSheetVerify.isCancelable = false
                                bottomSheetVerify.show(
                                    supportFragmentManager,
                                    "CarpoolActivity"
                                )
                            } else {
                                for (document in userDocuments) {
                                    if (document?.isVerified.toString().toInt() == 0) {
                                        flagVerified = false
                                        break
                                    }
                                }

                                loadingDialog.dismiss()
                                if (flagVerified) {
                                    val bottomSheetVerifySuccess = BottomSheetVerifySuccess()
                                    bottomSheetVerifySuccess.show(
                                        supportFragmentManager,
                                        "CarpoolActivity"
                                    )
//                                    val iAddCarpool = Intent(
//                                        this@CarpoolActivity,
//                                        AddCarpoolActivity::class.java
//                                    )
//                                    startActivity(iAddCarpool)
                                } else {
                                    val bottomSheetVerifyWaiting = BottomSheetVerifyWaiting()
                                    bottomSheetVerifyWaiting.show(
                                        supportFragmentManager,
                                        "CarpoolActivity"
                                    )
//                                    alertDialogMessage(
//                                        this@CarpoolActivity,
//                                        "Mohon tunggu sampai dokumen anda disetujui.",
//                                        "Peringatan"
//                                    )
                                }
                            }
                        }

                        is Result.Error -> {
                            loadingDialog.dismiss()
                            alertDialogMessage(
                                this@CarpoolActivity,
                                "Gagal mendapatkan info dokumen!",
                                "Error"
                            )
                        }
                    }
                }
            }

            btnVehicleCarpool.setOnClickListener {
                carpoolViewModel.getDocuments().observe(this@CarpoolActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            loadingDialog.show()
                        }

                        is Result.Success -> {
                            var flagVerified = true
                            val userDocuments = result.data.payload

                            Log.i("KLOOT", "setListeners: ${result.data}")

                            if (userDocuments!!.isEmpty()) {
                                loadingDialog.dismiss()
                                val bottomSheetVerify = BottomSheetVerify()
                                bottomSheetVerify.isCancelable = false
                                bottomSheetVerify.show(
                                    supportFragmentManager,
                                    "CarpoolActivity"
                                )
                            } else {
                                for (document in userDocuments) {
                                    if (document?.isVerified.toString().toInt() == 0) {
                                        flagVerified = false
                                        break
                                    }
                                }

                                loadingDialog.dismiss()
                                if (flagVerified) {
                                    val iVehicle = Intent(
                                        this@CarpoolActivity,
                                        VehicleActivity::class.java
                                    )
                                    startActivity(iVehicle)
                                } else {
                                    val bottomSheetVerifyWaiting = BottomSheetVerifyWaiting()
                                    bottomSheetVerifyWaiting.show(
                                        supportFragmentManager,
                                        "CarpoolActivity"
                                    )
                                }
                            }
                        }

                        is Result.Error -> {
                            loadingDialog.dismiss()
                            alertDialogMessage(
                                this@CarpoolActivity,
                                "Gagal mendapatkan info dokumen!",
                                "Error"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isAddValid(): Boolean {
        return if (carpoolViewModel.phoneNumberOnce() == preferenceDefaultValue) {
            alertDialogMessage(
                this@CarpoolActivity,
                "Lengkapi Profil anda (foto & nomor telepon) terlebih dahulu di update profil pada halaman pengaturan!",
                "Peringatan"
            )
            false
        } else {
            true
        }
    }

    private fun checkDocumentStatus() {
        carpoolViewModel.getDocuments().observe(this@CarpoolActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.show()
                }

                is Result.Success -> {
                    loadingDialog.dismiss()

                    var flagVerified = true
                    val userDocuments = result.data.payload

                    if (userDocuments == null) {
                        alertDialogMessage(
                            this@CarpoolActivity,
                            "Verifikasi Dokumen anda (KTP & STNK) terlebih dahulu di opsi menu Verifikasi!",
                            "Peringatan"
                        )
                    } else {
                        for (document in userDocuments) {
                            if (document?.isVerified.toString().toInt() == 0) {
                                flagVerified = false
                                break
                            }
                        }

                        if (flagVerified) {

                        } else {
                            alertDialogMessage(
                                this@CarpoolActivity,
                                "Mohon tunggu sampai dokumen anda disetujui.",
                                "Peringatan"
                            )
                        }
                    }
                }

                is Result.Error -> {
                    loadingDialog.dismiss()
                    alertDialogMessage(
                        this@CarpoolActivity,
                        "Gagal mendapatkan info dokumen!",
                        "Error"
                    )
                }
            }
        }
    }

    private fun setSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = true
                carpoolViewModel.getCarpoolingData()
//                isRefreshing = false
            }
        }
    }

    companion object {
        val tabTitles = intArrayOf(
            R.string.allCarpool,
            R.string.myCarpool
        )
    }
}