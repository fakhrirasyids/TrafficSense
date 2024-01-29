package com.lazycode.trafficsense.ui.carpool.verify

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentBottomSheetVerifyBinding
import com.lazycode.trafficsense.databinding.FragmentBottomSheetVerifySuccessBinding
import com.lazycode.trafficsense.databinding.FragmentBottomSheetVerifyWaitingBinding
import com.lazycode.trafficsense.ui.carpool.CarpoolActivity
import com.lazycode.trafficsense.ui.carpool.CarpoolViewModel
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground
import com.lazycode.trafficsense.utils.Constants.uriToFile
import com.lazycode.trafficsense.utils.Result

class BottomSheetVerifySuccess : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetVerifySuccessBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetVerifySuccessBinding.inflate(inflater, container, false)
        setTransparentBackground()

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
}