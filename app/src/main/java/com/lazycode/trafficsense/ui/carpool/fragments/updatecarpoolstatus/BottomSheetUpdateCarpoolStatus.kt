package com.lazycode.trafficsense.ui.carpool.fragments.updatecarpoolstatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.databinding.FragmentBottomSheetDepartureInfoBinding
import com.lazycode.trafficsense.databinding.FragmentBottomSheetUpdateCarpoolStatusBinding
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground

class BottomSheetUpdateCarpoolStatus : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetUpdateCarpoolStatusBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetUpdateCarpoolStatusBinding.inflate(inflater, container, false)
        setTransparentBackground()
        val carpoolStatus = this.arguments?.getInt(KEY_CARPOOL_STATUS, 1)

        binding.apply {
            when(carpoolStatus) {
                1 -> {
                    btnBerangkat.isVisible = true
                }
                2 -> {
                    btnBerangkat.isVisible = true
                }
                3 -> {
                    btnTiba.isVisible = true
                }
                4 -> {

                }
            }
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
        const val KEY_CARPOOL_STATUS = "key_carpool_status"
    }
}