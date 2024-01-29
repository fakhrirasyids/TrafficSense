package com.lazycode.trafficsense.ui.carpool.vehicle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityVehicleBinding
import com.lazycode.trafficsense.ui.adapter.VehicleAdapter
import com.lazycode.trafficsense.ui.carpool.vehicle.addvehicle.BottomSheetAddVehicle
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Result
import org.koin.android.viewmodel.ext.android.viewModel

class VehicleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleBinding
    val vehicleViewModel: VehicleViewModel by viewModel()

    private val vehicleAdapter = VehicleAdapter()
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        observeViewModel()

        setSwipeRefresh()
        setListeners()
        setRecyclerView()
    }

    private fun setSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = true
                vehicleViewModel.getVehicles()
            }
        }
    }

    private fun observeViewModel() {
        vehicleViewModel.apply {
            isLoading.observe(this@VehicleActivity) {
                binding.tvEmptyMessage.isVisible = false

                shimmerToggle(it)
                if (!it) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            vehicleList.observe(this@VehicleActivity) {
                binding.tvEmptyMessage.isVisible = it.isEmpty()
                binding.rvVehicle.isVisible = it.isNotEmpty()

                vehicleAdapter.submitList(it)
            }

            errorText.observe(this@VehicleActivity) {
                binding.tvEmptyMessage.isVisible = false

                binding.rvVehicle.isVisible = it.isEmpty()
                binding.layoutError.isVisible = it.isNotEmpty()
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }

            btnAddVehicle.setOnClickListener {
                val bottomSheetAddVehicle = BottomSheetAddVehicle()
                bottomSheetAddVehicle.isCancelable = false
                bottomSheetAddVehicle.show(
                    supportFragmentManager,
                    "VehicleActivity"
                )
            }
        }
    }

    private fun shimmerToggle(isStart: Boolean) {
        binding.shimmerLayout.apply {
            isVisible = isStart
            if (isStart) {
                startShimmer()
            } else {
                stopShimmer()
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvVehicle.apply {
            val layoutManagerModel = LinearLayoutManager(this@VehicleActivity)
            vehicleAdapter.onDeleteClick = { id, vehicleName ->
                val builder = AlertDialog.Builder(this@VehicleActivity)
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Peringatan")
                    setMessage("Apakah anda benar-benar ingin menghapus $vehicleName?")
                    setPositiveButton("Ya") { infoDialog, _ ->
                        infoDialog.dismiss()
                        vehicleViewModel.deleteVehicles(
                            id
                        )
                            .observe(this@VehicleActivity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        loadingDialog.show()
                                    }

                                    is Result.Success -> {
                                        loadingDialog.dismiss()
                                        val builderInfo = AlertDialog.Builder(this@VehicleActivity)
                                        builderInfo.setCancelable(false)

                                        with(builderInfo)
                                        {
                                            setTitle("Sukses Menghapus $vehicleName")
                                            setMessage("Klik OK untuk melanjutkan.")
                                            setPositiveButton("OK") { infoDialogChild, _ ->
                                                vehicleViewModel.getVehicles()
                                                infoDialogChild.dismiss()
                                                infoDialog.dismiss()
                                            }
                                            show()
                                        }
                                    }

                                    is Result.Error -> {
                                        loadingDialog.dismiss()
                                        Constants.alertDialogMessage(
                                            this@VehicleActivity,
                                            result.error,
                                            "Gagal menghapus kendaraan."
                                        )
                                    }
                                }
                            }
                    }
                    setNegativeButton("Tidak") { infoDialog, _ ->
                        infoDialog.dismiss()
                    }
                    show()
                }
            }

            adapter = vehicleAdapter
            layoutManager = layoutManagerModel
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    layoutManagerModel.orientation
                )
            )
        }
    }
}