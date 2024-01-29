package com.lazycode.trafficsense.ui.profilesettings.editprofile

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentBottomSheetEditProfileBinding
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsViewModel
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground
import com.lazycode.trafficsense.utils.Constants.uriToFile
import com.lazycode.trafficsense.utils.Result
import com.lazycode.trafficsense.utils.UserPreferences

class BottomSheetEditProfile : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileSettingsViewModel: ProfileSettingsViewModel

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val image = result.data?.data as Uri
            profileSettingsViewModel.imageProfile.postValue(uriToFile(requireContext(), image))
        }
    }

    private lateinit var loadingDialog: AlertDialog

//    override fun onStart() {
//        super.onStart()
//        val sheetContainer = requireView().parent as? ViewGroup ?: return
//        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetEditProfileBinding.inflate(inflater, container, false)
        val inflaterLoading: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoading.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()

        profileSettingsViewModel =
            (requireActivity() as ProfileSettingsActivity).profileSettingsViewModel

        setTransparentBackground()

        observeForm()

        setupBackPressListener()
        setListeners()

        return binding.root
    }

    private fun observeForm() {
        profileSettingsViewModel.apply {
            imageProfile.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.ivProfile.setImageBitmap(BitmapFactory.decodeFile(it.path))
                }
            }

            getFullname().observe(viewLifecycleOwner) {
                if (it != UserPreferences.preferenceDefaultValue) {
                    binding.edName.setText(it)
                }
            }

            getPhone().observe(viewLifecycleOwner) {
                if (it != UserPreferences.preferenceDefaultValue) {
                    binding.edPhoneNumber.setText(it)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Peringatan")
                    setMessage("Batalkan sesi Update Profil?")
                    setPositiveButton("Ya") { _, _ ->
                        profileSettingsViewModel.imageProfile.postValue(null)
                        dismiss()
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }

            btnChangeImage.setOnClickListener {
                val iGallery = Intent()
                iGallery.action = Intent.ACTION_GET_CONTENT
                iGallery.type = "image/*"
                galleryLauncher.launch(Intent.createChooser(iGallery, "Pilih Foto Profil"))
            }

            btnSave.setOnClickListener {
                if (isValid()) {
                    profileSettingsViewModel.updateProfile(
                        edName.text.toString(),
                        edPhoneNumber.text.toString(),
                    ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {
                                profileSettingsViewModel.savePhoneProfilePic(
                                    result.data.payload?.name.toString(),
                                    result.data.payload?.phoneNumber.toString(),
                                    result.data.payload?.profilePictureUrl.toString(),
                                )

                                loadingDialog.dismiss()
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setCancelable(false)

                                with(builder)
                                {
                                    setTitle("Sukses menyimpan Info Profil!")
                                    setMessage("Klik OK untuk melanjutkan.")
                                    setPositiveButton("OK") { infoDialog, _ ->
                                        infoDialog.dismiss()
                                        dismiss()
                                    }
                                    show()
                                }
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()
                                alertDialogMessage(requireContext(), result.error, "Gagal menyimpan info profil")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        return if (profileSettingsViewModel.imageProfile.value == null) {
            alertDialogMessage(requireContext(), "Pilih Foto Profil terlebih dahulu!")
            false
        } else if (binding.edName.text.isNullOrEmpty()) {
            alertDialogMessage(requireContext(), "Isi Nama Lengkap terlebih dahulu!")
            false
        } else if (binding.edPhoneNumber.text.isNullOrEmpty()) {
            alertDialogMessage(requireContext(), "Isi Nomor Telepon terlebih dahulu!")
            false
        } else {
            true
        }
    }

    private fun setupBackPressListener() {
        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        (requireActivity() as MainActivity).refreshLayoutData()
//        super.onDismiss(dialog)
//    }
}