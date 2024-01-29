package com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentBottomSheetNegotiateBinding
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengerViewModel
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengersActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground
import com.lazycode.trafficsense.utils.Result

class BottomSheetNegotiate : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetNegotiateBinding? = null
    private val binding get() = _binding!!
    private lateinit var driverPassengerViewModel: DriverPassengerViewModel
    private lateinit var loadingDialog: AlertDialog

    private  var passengerId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetNegotiateBinding.inflate(inflater, container, false)
        val inflaterLoading: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoading.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()

        driverPassengerViewModel =
            (requireActivity() as DriverPassengersActivity).driverPassengerViewModel
        passengerId = this.requireArguments().getInt(KEY_PASSENGER_ID,-1)

        setTransparentBackground()

        setupBackPressListener()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Peringatan")
                    setMessage("Batalkan sesi Negosiasi Harga?")
                    setPositiveButton("Ya") { _, _ ->
                        dismiss()
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }


            btnSave.setOnClickListener {
                if (isValid()) {
                    driverPassengerViewModel.updatePassengerPrice(
                        passengerId,
                        edPrice.text.toString().toInt(),
                    ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {

                                loadingDialog.dismiss()

                                if (result.data.success!!) {
                                    val builder = AlertDialog.Builder(requireContext())
                                    builder.setCancelable(false)

                                    with(builder)
                                    {
                                        setTitle("Sukses Mengirimkan Pengajuan Negosiasi!")
                                        setMessage("Tunggu calon penumpang untuk merespon harga negosiasi.")
                                        setPositiveButton("OK") { infoDialog, _ ->
                                            driverPassengerViewModel.getDriverPassengers()
                                            infoDialog.dismiss()
                                            dismiss()
                                        }
                                        show()
                                    }
                                } else {
                                    alertDialogMessage(requireContext(), result.data.message.toString(), "Gagal")
                                }
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()
                                alertDialogMessage(requireContext(), "Kapasitas sudah penuh.", "Gagal")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        return if (binding.edPrice.text.toString().isEmpty()) {
            alertDialogMessage(requireContext(), "Isi pengajuan harga terlebih dahulu!")
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

    companion object {
        const val KEY_PASSENGER_ID = "key_passenger_id"
    }
}