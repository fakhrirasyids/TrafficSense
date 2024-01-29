package com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityDriverPassengersBinding
import com.lazycode.trafficsense.ui.adapter.DriverPassengerAdapter
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments.BottomSheetNegotiate
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments.BottomSheetNegotiate.Companion.KEY_PASSENGER_ID
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments.BottomSheetPickInfo
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments.BottomSheetPickInfo.Companion.KEY_PICK_INFO
import com.lazycode.trafficsense.utils.Constants
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DriverPassengersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverPassengersBinding
    private val carpoolId by lazy { intent.getIntExtra(KEY_CARPOOL_ID, -1) }
    val driverPassengerViewModel: DriverPassengerViewModel by viewModel {
        parametersOf(
            carpoolId
        )
    }

    private val acceptedDriverPassengerAdapter = DriverPassengerAdapter()
    private val negotiateDriverPassengerAdapter = DriverPassengerAdapter()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverPassengersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        binding.toolbar.setNavigationOnClickListener { finish() }

        observeViewModel()
        setRecyclerView()

        setupPlayAnimation()
    }

    private fun observeViewModel() {
        driverPassengerViewModel.apply {
            isLoading.observe(this@DriverPassengersActivity) {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }

            driverPassengerListAccept.observe(this@DriverPassengersActivity) {
                binding.acceptLayout.isVisible = it.isNotEmpty()

                acceptedDriverPassengerAdapter.submitList(it)
            }

            driverPassengerListNegotiate.observe(this@DriverPassengersActivity) { negotiateList ->
                binding.negotiateLayout.isVisible = negotiateList.isNotEmpty()

                negotiateDriverPassengerAdapter.submitList(
                    negotiateList.sortedWith(
                        compareBy(
                            { it.price != null },
                            { it.price })
                    )
                )
            }

            errorText.observe(this@DriverPassengersActivity) {
//                binding.rvVehicle.isVisible = it.isEmpty()
//                binding.layoutError.isVisible = it.isNotEmpty()
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvPassengerAccept.apply {
            val layoutManagerModel = LinearLayoutManager(this@DriverPassengersActivity)

            acceptedDriverPassengerAdapter.onJemputClick = { pickInfo, dropInfo ->
                val bundle = Bundle()
                bundle.putString(KEY_PICK_INFO, pickInfo)
                bundle.putString(BottomSheetPickInfo.KEY_DESTINATION_INFO, dropInfo)
                val bottomSheetPickInfo = BottomSheetPickInfo()
                bottomSheetPickInfo.arguments = bundle
                bottomSheetPickInfo.show(
                    supportFragmentManager,
                    "DriverPassengerActivity"
                )
            }

            acceptedDriverPassengerAdapter.onContactClick = { driverName, contact ->
                Constants.openWhatsappHelper(
                    this@DriverPassengersActivity,
                    this@DriverPassengersActivity,
                    driverName,
                    contact
                )
            }

            adapter = acceptedDriverPassengerAdapter
            layoutManager = layoutManagerModel
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    layoutManagerModel.orientation
                )
            )
        }

        binding.rvPassengerNegotiate.apply {
            val layoutManagerModel = LinearLayoutManager(this@DriverPassengersActivity)

            negotiateDriverPassengerAdapter.onJemputClick = { pickInfo, dropInfo ->
                val bundle = Bundle()
                bundle.putString(KEY_PICK_INFO, pickInfo)
                bundle.putString(BottomSheetPickInfo.KEY_DESTINATION_INFO, dropInfo)
                val bottomSheetPickInfo = BottomSheetPickInfo()
                bottomSheetPickInfo.arguments = bundle
                bottomSheetPickInfo.show(
                    supportFragmentManager,
                    "DriverPassengerActivity"
                )
            }
            negotiateDriverPassengerAdapter.onContactClick = { driverName, contact ->
                Constants.openWhatsappHelper(
                    this@DriverPassengersActivity,
                    this@DriverPassengersActivity,
                    driverName,
                    contact
                )
            }

            negotiateDriverPassengerAdapter.onUpdatePriceClick = { passengerId ->
                val bundle = Bundle()
                bundle.putInt(KEY_PASSENGER_ID, passengerId)
                val bottomSheetNegotiate = BottomSheetNegotiate()
                bottomSheetNegotiate.arguments = bundle
                bottomSheetNegotiate.isCancelable = false
                bottomSheetNegotiate.show(
                    supportFragmentManager,
                    "DriverPassengersActivity"
                )
            }

            adapter = negotiateDriverPassengerAdapter
            layoutManager = layoutManagerModel
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    layoutManagerModel.orientation
                )
            )
        }
    }


    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val acceptLayout: Animator =
            ObjectAnimator.ofFloat(binding.acceptLayout, View.ALPHA, 1f).setDuration(300)
        val negotiateLayout: Animator =
            ObjectAnimator.ofFloat(binding.negotiateLayout, View.ALPHA, 1f).setDuration(300)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(acceptLayout, negotiateLayout)
        animatorSet.startDelay = 200
        animatorSet.start()
    }

    companion object {
        const val KEY_CARPOOL_ID = "key_carpool_id"
    }
}