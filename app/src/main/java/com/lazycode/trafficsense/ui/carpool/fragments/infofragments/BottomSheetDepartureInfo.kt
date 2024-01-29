package com.lazycode.trafficsense.ui.carpool.fragments.infofragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.databinding.FragmentBottomSheetDepartureInfoBinding
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground

class BottomSheetDepartureInfo : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetDepartureInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDepartureInfoBinding.inflate(inflater, container, false)
        setTransparentBackground()
        val departureTime = this.arguments?.getString(KEY_DEPARTURE_TIME)
        val departureInfo = this.arguments?.getString(KEY_DEPARTURE_INFO)
        val destinationInfo = this.arguments?.getString(KEY_DESTINATION_INFO)

        binding.apply {
            tvDepartureTime.text = departureTime
            tvDepartureInfo.text = departureInfo
            tvDestinationInfo.text = destinationInfo
        }

        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val KEY_DEPARTURE_TIME = "key_departure_time"
        const val KEY_DEPARTURE_INFO = "key_departure_info"
        const val KEY_DESTINATION_INFO = "key_destination_info"
    }
}