package com.lazycode.trafficsense.ui.profilesettings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.lazycode.trafficsense.utils.Result
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityProfileSettingsBinding
import com.lazycode.trafficsense.ui.profilesettings.editprofile.BottomSheetEditProfile
import com.lazycode.trafficsense.ui.splash.SplashActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.UserPreferences.Companion.preferenceDefaultValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingsBinding
    val profileSettingsViewModel: ProfileSettingsViewModel by viewModel()
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        setListeners()
        getProfileInfo()
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }

            btnEditProfile.setOnClickListener {
                val bottomSheetEditProfile = BottomSheetEditProfile()
                bottomSheetEditProfile.isCancelable = false
                bottomSheetEditProfile.show(
                    supportFragmentManager,
                    "ProfileSettingsBottomSheet"
                )
//                BottomSheetTambahKandang.bottomSheetTambahKandang =
//                    bottomSheetEditProfile
            }

            btnLogout.setOnClickListener {
                val builder = AlertDialog.Builder(this@ProfileSettingsActivity)
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Peringatan")
                    setMessage("Apakah anda ingin Log Out?")

                    setPositiveButton("Ya") { dialog, _ ->
                        dialog.dismiss()
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result = profileSettingsViewModel.logout()

                            withContext(Dispatchers.Main) {
                                result.observe(this@ProfileSettingsActivity) { result ->
                                    when (result) {
                                        is Result.Loading -> {
                                            loadingDialog.show()
                                        }

                                        is Result.Success -> {
                                            profileSettingsViewModel.clearPreferences()
                                            loadingDialog.dismiss()
                                            val infoSuccessBuilder =
                                                AlertDialog.Builder(this@ProfileSettingsActivity)
                                            infoSuccessBuilder.setCancelable(false)

                                            with(infoSuccessBuilder)
                                            {
                                                setMessage("Berhasil Log Out!")

                                                setPositiveButton("Lanjut") { dialog, _ ->
                                                    dialog.dismiss()
                                                    finishAffinity()
                                                    startActivity(
                                                        Intent(
                                                            this@ProfileSettingsActivity,
                                                            SplashActivity::class.java
                                                        )
                                                    )
                                                }
                                                show()
                                            }
                                        }

                                        is Result.Error -> {
                                            loadingDialog.dismiss()
                                            alertDialogMessage(
                                                this@ProfileSettingsActivity,
                                                result.error,
                                                "Error"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        }
    }

    private fun getProfileInfo() {
        profileSettingsViewModel.apply {
            getProfilePic().observe(this@ProfileSettingsActivity) {
                if (it != preferenceDefaultValue) {
                    Glide.with(this@ProfileSettingsActivity)
                        .load(it)
                        .into(binding.ivAvatar)
                }
            }

            getFullname().observe(this@ProfileSettingsActivity) {
                if (it != preferenceDefaultValue) {
                    binding.tvName.text = it
                }
            }

            getEmail().observe(this@ProfileSettingsActivity) {
                if (it != preferenceDefaultValue) {
                    binding.tvEmail.text = it
                }
            }
        }
    }
}