package com.lazycode.trafficsense.ui.carpool.vehicle.addvehicle

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
import com.lazycode.trafficsense.databinding.FragmentBottomSheetAddVehicleBinding
import com.lazycode.trafficsense.ui.carpool.vehicle.VehicleActivity
import com.lazycode.trafficsense.ui.carpool.vehicle.VehicleViewModel
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground
import com.lazycode.trafficsense.utils.Constants.uriToFile
import com.lazycode.trafficsense.utils.Result

class BottomSheetAddVehicle : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetAddVehicleBinding? = null
    private val binding get() = _binding!!
    private lateinit var vehicleViewModel: VehicleViewModel

    private val galleryVehicleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val image = result.data?.data as Uri
            vehicleViewModel.vehicleImage.postValue(uriToFile(requireContext(), image))
        }
    }

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetAddVehicleBinding.inflate(inflater, container, false)
        val inflaterLoading: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoading.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()

        vehicleViewModel =
            (requireActivity() as VehicleActivity).vehicleViewModel

        setTransparentBackground()

        observeForm()

        setupBackPressListener()
        setListeners()

        return binding.root
    }

    private fun observeForm() {
        vehicleViewModel.apply {
            vehicleImage.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.ivVehicle.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.ivVehicle.setImageBitmap(BitmapFactory.decodeFile(it.path))
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
                    setMessage("Batalkan sesi Tambah Kendaraan?")
                    setPositiveButton("Ya") { _, _ ->
                        vehicleViewModel.vehicleImage.postValue(null)
                        dismiss()
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }

            ivVehicle.setOnClickListener {
                val iGallery = Intent()
                iGallery.action = Intent.ACTION_GET_CONTENT
                iGallery.type = "image/*"
                galleryVehicleLauncher.launch(Intent.createChooser(iGallery, "Pilih Foto Kendaraan"))
            }


            btnSave.setOnClickListener {
                if (isValid()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Peringatan")
                        setMessage("Apakah data Kendaraan sudah benar?")
                        setPositiveButton("Ya") { infoDialog, _ ->
                            infoDialog.dismiss()
                            vehicleViewModel.storeVehicles(
                                edName.text.toString(),
                                edCapacity.text.toString()
                            )
                                .observe(viewLifecycleOwner) { result ->
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
                                                setTitle("Sukses Menambahkan Kendaraan")
                                                setMessage("Klik OK untuk melanjutkan.")
                                                setPositiveButton("OK") { infoDialog, _ ->
                                                    vehicleViewModel.vehicleImage.postValue(null)
                                                    vehicleViewModel.getVehicles()
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
                                                "Gagal menambahkan kendaraan."
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
            }
        }
    }

    private fun isValid(): Boolean {
        return if (vehicleViewModel.vehicleImage.value == null) {
            alertDialogMessage(requireContext(), "Pilih Foto SIM terlebih dahulu!")
            false
        } else if (binding.edName.text.toString().isEmpty()) {
            alertDialogMessage(requireContext(), "Isi Nama Kendaraan dengan benar!")
            false
        } else if (binding.edCapacity.text.toString().isEmpty()) {
            alertDialogMessage(requireContext(), "Isi Kapasitas Kendaraan dengan benar!")
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
}