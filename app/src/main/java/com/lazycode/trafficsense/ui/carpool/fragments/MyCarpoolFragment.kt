package com.lazycode.trafficsense.ui.carpool.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.CarpoolingItem
import com.lazycode.trafficsense.databinding.FragmentMyCarpoolBinding
import com.lazycode.trafficsense.ui.adapter.MyCarpoolAdapter
import com.lazycode.trafficsense.ui.carpool.CarpoolActivity
import com.lazycode.trafficsense.ui.carpool.CarpoolViewModel
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengersActivity
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengersActivity.Companion.KEY_CARPOOL_ID
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetDepartureInfo
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetVehicleInfo
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result

class MyCarpoolFragment : Fragment() {
    private var _binding: FragmentMyCarpoolBinding? = null
    private val binding get() = _binding!!
    private lateinit var carpoolViewModel: CarpoolViewModel

    private val myCarpoolAdapter = MyCarpoolAdapter()
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCarpoolBinding.inflate(inflater, container, false)
        val inflaterLoading: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoading.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()

        carpoolViewModel = (requireActivity() as CarpoolActivity).carpoolViewModel

        observeViewModel()
        setRecyclerView()

        return binding.root
    }

    private fun observeViewModel() {
        carpoolViewModel.apply {
            isLoadingMyCarpool.observe(viewLifecycleOwner) {
                binding.tvEmptyMessage.isVisible = false

                shimmerToggle(it)
                if (!it) {
                    (requireActivity() as CarpoolActivity).binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            myCarpoolList.observe(viewLifecycleOwner) { carpoolList ->
                binding.tvEmptyMessage.isVisible = carpoolList.isEmpty()
                binding.rvCarpool.isVisible = carpoolList.isNotEmpty()

                val sortedAwal = carpoolList.sortedBy { it.status.toString().toInt() }
                val sortedAkhir = sortedAwal.sortedWith(compareByDescending<CarpoolingItem> {
                    it.status.toString().toInt() == 1
                }
                    .thenByDescending { it.id })

                myCarpoolAdapter.submitList(sortedAkhir) {
                    binding.rvCarpool.scrollToPosition(0)
                }
            }

            errorTextMyCarpoolingItem.observe(viewLifecycleOwner) {
                binding.tvEmptyMessage.isVisible = false

                binding.rvCarpool.isVisible = it.isEmpty()
                binding.layoutError.isVisible = it.isNotEmpty()
            }
        }
    }

    private fun shimmerToggle(isStart: Boolean) {
        binding.apply {
            layoutShimmerParent.isVisible = isStart
            if (isStart) {
                shimmerLayout.startShimmer()
            } else {
                shimmerLayout.stopShimmer()
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvCarpool.apply {
            val layoutManagerModel = LinearLayoutManager(requireContext())

            myCarpoolAdapter.onUpdateClick = { carpoolId, carpoolStatus ->
                var changeToStatus = ""
                var changeStatusFlag = 1

                when (carpoolStatus) {
                    1 -> {
                        changeToStatus = "Berangkat"
                        changeStatusFlag = 3
                    }
                    2 -> {
                        changeToStatus = "Berangkat"
                        changeStatusFlag = 3
                    }
                    3 -> {
                        changeToStatus = "Tiba"
                        changeStatusFlag = 4
                    }
                    4 -> {
                        changeToStatus = "Selesai"
                        changeStatusFlag = 5
                    }
                }

                val builder = AlertDialog.Builder(requireContext())
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Peringatan")
                    setMessage("Apakah anda ingin mengubah status menjadi $changeToStatus?")
                    setPositiveButton("Ya") { dialog, _ ->
                        dialog.dismiss()
                        carpoolViewModel.updateCarpoolStatus(carpoolId, changeStatusFlag).observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()
                                    if (result.data.success!!) {
                                        val infoBuilder = AlertDialog.Builder(requireContext())
                                        infoBuilder.setCancelable(false)

                                        with(infoBuilder)
                                        {
                                            setTitle("Sukses Update Status ke $changeToStatus")
                                            setMessage("Klik OK untuk melanjutkan.")
                                            setPositiveButton("OK") { dialog, _ ->
                                                carpoolViewModel.getCarpoolingData()
                                                dialog.dismiss()
                                            }
                                            show()
                                        }
                                    } else {
                                        alertDialogMessage(requireContext(), result.data.message.toString(), "Gagal")
                                    }
                                }

                                is Result.Error -> {
                                    loadingDialog.dismiss()
                                    alertDialogMessage(requireContext(), result.error, "Error")
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

            myCarpoolAdapter.onApplyClick = { carpoolId ->
                val iDriverPassenger =
                    Intent(requireActivity(), DriverPassengersActivity::class.java)
                iDriverPassenger.putExtra(KEY_CARPOOL_ID, carpoolId)
                requireActivity().startActivity(iDriverPassenger)
            }

            myCarpoolAdapter.onDepartureInfoClick = { time, departure, arrive ->
                val bundle = Bundle()
                bundle.putString(BottomSheetDepartureInfo.KEY_DEPARTURE_TIME, time)
                bundle.putString(BottomSheetDepartureInfo.KEY_DEPARTURE_INFO, departure)
                bundle.putString(BottomSheetDepartureInfo.KEY_DESTINATION_INFO, arrive)
                val bottomSheetDepartureInfo = BottomSheetDepartureInfo()
                bottomSheetDepartureInfo.arguments = bundle
                bottomSheetDepartureInfo.show(
                    childFragmentManager,
                    "CarpoolItem"
                )
            }

            myCarpoolAdapter.onVehicleInfoClick = { name, capacity ->
                val bundle = Bundle()
                bundle.putString(BottomSheetVehicleInfo.KEY_VEHICLE_NAME, name)
                bundle.putString(BottomSheetVehicleInfo.KEY_VEHICLE_CAPACITY, capacity)
                val bottomSheetVehicleInfo = BottomSheetVehicleInfo()
                bottomSheetVehicleInfo.arguments = bundle
                bottomSheetVehicleInfo.show(
                    childFragmentManager,
                    "CarpoolItem"
                )
            }

            adapter = myCarpoolAdapter
            layoutManager = layoutManagerModel
        }
    }
}