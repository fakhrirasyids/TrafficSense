package com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.databinding.FragmentBottomSheetPickInfoBinding
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground

class BottomSheetPickInfo : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetPickInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetPickInfoBinding.inflate(inflater, container, false)
        setTransparentBackground()
        val pickInfo = this.arguments?.getString(KEY_PICK_INFO)
        val destinationInfo = this.arguments?.getString(KEY_DESTINATION_INFO)

        binding.apply {
            tvPickInfo.text = pickInfo
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
        const val KEY_PICK_INFO = "key_pick_info"
        const val KEY_DESTINATION_INFO = "key_destination_info"
    }
}