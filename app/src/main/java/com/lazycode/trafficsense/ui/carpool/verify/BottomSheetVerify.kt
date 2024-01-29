package com.lazycode.trafficsense.ui.carpool.verify

import android.app.Dialog
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentBottomSheetVerifyBinding
import com.lazycode.trafficsense.ui.carpool.CarpoolActivity
import com.lazycode.trafficsense.ui.carpool.CarpoolViewModel
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground
import com.lazycode.trafficsense.utils.Constants.uriToFile
import com.lazycode.trafficsense.utils.Result

class BottomSheetVerify : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetVerifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var carpoolViewModel: CarpoolViewModel

    private val galleryKtpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val image = result.data?.data as Uri
            carpoolViewModel.ktpImage.postValue(uriToFile(requireContext(), image))
        }
    }

    private val gallerySimLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val image = result.data?.data as Uri
            carpoolViewModel.simImage.postValue(uriToFile(requireContext(), image))
        }
    }

    private val galleryStnkLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val image = result.data?.data as Uri
            carpoolViewModel.stnkImage.postValue(uriToFile(requireContext(), image))
        }
    }

    private lateinit var loadingDialog: AlertDialog

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = resources.displayMetrics.heightPixels
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetVerifyBinding.inflate(inflater, container, false)
        val inflaterLoading: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoading.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()

        carpoolViewModel =
            (requireActivity() as CarpoolActivity).carpoolViewModel

        setTransparentBackground()

        observeForm()

        setupBackPressListener()
        setListeners()

        return binding.root
    }

    private fun observeForm() {
        carpoolViewModel.apply {
            ktpImage.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.ivKtpPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.ivKtpPhoto.setImageBitmap(BitmapFactory.decodeFile(it.path))
                }
            }

            simImage.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.ivSimPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.ivSimPhoto.setImageBitmap(BitmapFactory.decodeFile(it.path))
                }
            }

            stnkImage.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.ivStnkPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.ivStnkPhoto.setImageBitmap(BitmapFactory.decodeFile(it.path))
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
                    setMessage("Batalkan sesi Verifikasi Dokumen?")
                    setPositiveButton("Ya") { _, _ ->
                        carpoolViewModel.simImage.postValue(null)
                        carpoolViewModel.stnkImage.postValue(null)
                        dismiss()
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }

            ivKtpPhoto.setOnClickListener {
                val iGallery = Intent()
                iGallery.action = Intent.ACTION_GET_CONTENT
                iGallery.type = "image/*"
                galleryKtpLauncher.launch(Intent.createChooser(iGallery, "Pilih Foto SIM"))
            }

            ivSimPhoto.setOnClickListener {
                val iGallery = Intent()
                iGallery.action = Intent.ACTION_GET_CONTENT
                iGallery.type = "image/*"
                gallerySimLauncher.launch(Intent.createChooser(iGallery, "Pilih Foto SIM"))
            }

            ivStnkPhoto.setOnClickListener {
                val iGallery = Intent()
                iGallery.action = Intent.ACTION_GET_CONTENT
                iGallery.type = "image/*"
                galleryStnkLauncher.launch(Intent.createChooser(iGallery, "Pilih Foto STNK"))
            }


            btnSave.setOnClickListener {
                if (isValid()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Peringatan")
                        setMessage("Apakah data KTP, SIM, dan STNK sudah benar?")
                        setPositiveButton("Ya") { infoDialog, _ ->
                            infoDialog.dismiss()
                            carpoolViewModel.storeDocuments()
                                .observe(this@BottomSheetVerify) { result ->
                                    when (result) {
                                        is Result.Loading -> {
                                            loadingDialog.show()
                                        }

                                        is Result.Success -> {
                                            loadingDialog.dismiss()
                                            val builderInfo = AlertDialog.Builder(requireContext())
                                            builderInfo.setCancelable(false)

                                            with(builderInfo)
                                            {
                                                setTitle("Sukses Mengirim Dokumen")
                                                setMessage("Mohon tunggu maksimal 1x24 jam di tahap pemverifikasian dokumen ini.")
                                                setPositiveButton("OK") { infoDialog, _ ->
                                                    infoDialog.dismiss()
                                                    dismiss()
                                                }
                                                show()
                                            }
                                        }

                                        is Result.Error -> {
                                            loadingDialog.dismiss()
                                            alertDialogMessage(
                                                requireContext(),
                                                result.error,
                                                "Gagal menyimpan info profil"
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
//                    carpoolViewModel.updateProfile(
//                        edName.text.toString(),
//                        edPhoneNumber.text.toString(),
//                    ).observe(viewLifecycleOwner) { result ->
//                        when (result) {
//                            is Result.Loading -> {
//                                loadingDialog.show()
//                            }
//
//                            is Result.Success -> {
//                                carpoolViewModel.savePhoneProfilePic(
//                                    result.data.payload?.name.toString(),
//                                    result.data.payload?.phoneNumber.toString(),
//                                    result.data.payload?.profilePictureUrl.toString(),
//                                )
//
//                                loadingDialog.dismiss()
//                                val builder = AlertDialog.Builder(requireContext())
//                                builder.setCancelable(false)
//
//                                with(builder)
//                                {
//                                    setTitle("Sukses menyimpan Info Profil!")
//                                    setMessage("Klik OK untuk melanjutkan.")
//                                    setPositiveButton("OK") { infoDialog, _ ->
//                                        infoDialog.dismiss()
//                                        dismiss()
//                                    }
//                                    show()
//                                }
//                            }
//
//                            is Result.Error -> {
//                                loadingDialog.dismiss()
//                                alertDialogMessage(requireContext(), result.error, "Gagal menyimpan info profil")
//                            }
//                        }
//                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        return if (carpoolViewModel.ktpImage.value == null) {
            alertDialogMessage(requireContext(), "Pilih Foto KTP terlebih dahulu!")
            false
        } else if (carpoolViewModel.simImage.value == null) {
            alertDialogMessage(requireContext(), "Pilih Foto SIM terlebih dahulu!")
            false
        } else if (carpoolViewModel.stnkImage.value == null) {
            alertDialogMessage(requireContext(), "Pilih Foto STNK terlebih dahulu!")
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