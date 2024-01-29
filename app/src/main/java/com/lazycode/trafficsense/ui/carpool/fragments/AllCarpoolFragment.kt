package com.lazycode.trafficsense.ui.carpool.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazycode.trafficsense.data.models.CarpoolingItem
import com.lazycode.trafficsense.databinding.FragmentAllCarpoolBinding
import com.lazycode.trafficsense.ui.adapter.AllCarpoolAdapter
import com.lazycode.trafficsense.ui.carpool.CarpoolActivity
import com.lazycode.trafficsense.ui.carpool.CarpoolViewModel
import com.lazycode.trafficsense.ui.carpool.fragments.applyfragment.BottomSheetApplyCarpool
import com.lazycode.trafficsense.ui.carpool.fragments.applyfragment.BottomSheetApplyCarpool.Companion.KEY_CAPACITY
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengersActivity.Companion.KEY_CARPOOL_ID
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetDepartureInfo
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetDepartureInfo.Companion.KEY_DEPARTURE_INFO
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetDepartureInfo.Companion.KEY_DEPARTURE_TIME
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetDepartureInfo.Companion.KEY_DESTINATION_INFO
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetVehicleInfo
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetVehicleInfo.Companion.KEY_VEHICLE_CAPACITY
import com.lazycode.trafficsense.ui.carpool.fragments.infofragments.BottomSheetVehicleInfo.Companion.KEY_VEHICLE_NAME
import com.lazycode.trafficsense.utils.Constants.openWhatsappHelper


class AllCarpoolFragment : Fragment() {
    private var _binding: FragmentAllCarpoolBinding? = null
    private val binding get() = _binding!!
    private lateinit var carpoolViewModel: CarpoolViewModel

    private val allCarpoolAdapter = AllCarpoolAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllCarpoolBinding.inflate(inflater, container, false)
        carpoolViewModel = (requireActivity() as CarpoolActivity).carpoolViewModel

        observeViewModel()
        setRecyclerView()

        return binding.root
    }

    private fun observeViewModel() {
        carpoolViewModel.apply {
            isLoadingAllCarpool.observe(viewLifecycleOwner) {
                binding.tvEmptyMessage.isVisible = false

                shimmerToggle(it)

                if (!it) {
                    (requireActivity() as CarpoolActivity).binding.swipeRefreshLayout.isRefreshing =
                        false
                }
            }

            allCarpoolList.observe(viewLifecycleOwner) { carpoolList ->
                binding.tvEmptyMessage.isVisible = carpoolList.isEmpty()
                binding.rvCarpool.isVisible = carpoolList.isNotEmpty()

                val sortedAwal = carpoolList.sortedBy { it.status.toString().toInt() }
                val sortedAkhir = sortedAwal.sortedWith(compareByDescending<CarpoolingItem> {
                    it.status.toString().toInt() == 1
                }
                    .thenByDescending { it.id })

                allCarpoolAdapter.submitList(sortedAkhir) {
                    binding.rvCarpool.scrollToPosition(0)
                }
            }

            errorTextAllCarpoolingItem.observe(viewLifecycleOwner) {
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

            allCarpoolAdapter.onApplyClick = { carpoolId, carpoolCapacity ->
                val bundle = Bundle()
                bundle.putInt(KEY_CARPOOL_ID, carpoolId)
                bundle.putInt(KEY_CAPACITY, carpoolCapacity)
                val bottomSheetApplyCarpool = BottomSheetApplyCarpool()
                bottomSheetApplyCarpool.arguments = bundle
                bottomSheetApplyCarpool.isCancelable = false
                bottomSheetApplyCarpool.show(
                    childFragmentManager,
                    "CarpoolItem"
                )
            }

            allCarpoolAdapter.onContactClick = { driverName, contact ->
                openWhatsappHelper(requireActivity(), requireContext(), driverName, contact)
            }

            allCarpoolAdapter.onDepartureInfoClick = { time, departure, arrive ->
                val bundle = Bundle()
                bundle.putString(KEY_DEPARTURE_TIME, time)
                bundle.putString(KEY_DEPARTURE_INFO, departure)
                bundle.putString(KEY_DESTINATION_INFO, arrive)
                val bottomSheetDepartureInfo = BottomSheetDepartureInfo()
                bottomSheetDepartureInfo.arguments = bundle
                bottomSheetDepartureInfo.show(
                    childFragmentManager,
                    "CarpoolItem"
                )
            }

            allCarpoolAdapter.onVehicleInfoClick = { name, capacity ->
                val bundle = Bundle()
                bundle.putString(KEY_VEHICLE_NAME, name)
                bundle.putString(KEY_VEHICLE_CAPACITY, capacity)
                val bottomSheetVehicleInfo = BottomSheetVehicleInfo()
                bottomSheetVehicleInfo.arguments = bundle
                bottomSheetVehicleInfo.show(
                    childFragmentManager,
                    "CarpoolItem"
                )
            }

            adapter = allCarpoolAdapter
            layoutManager = layoutManagerModel
        }
    }
}