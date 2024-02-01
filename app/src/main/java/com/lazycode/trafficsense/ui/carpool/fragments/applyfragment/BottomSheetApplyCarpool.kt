package com.lazycode.trafficsense.ui.carpool.fragments.applyfragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.Point
import com.lazycode.trafficsense.databinding.FragmentBottomSheetApplyCarpoolBinding
import com.lazycode.trafficsense.databinding.FragmentBottomSheetVerifyBinding
import com.lazycode.trafficsense.ui.carpool.CarpoolActivity
import com.lazycode.trafficsense.ui.carpool.CarpoolViewModel
import com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers.DriverPassengersActivity.Companion.KEY_CARPOOL_ID
import com.lazycode.trafficsense.ui.dynamicroute.DynamicRouteViewModel
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.setTransparentBackground
import com.lazycode.trafficsense.utils.Constants.uriToFile
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class BottomSheetApplyCarpool : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetApplyCarpoolBinding? = null
    private val binding get() = _binding!!

    private val dynamicRouteViewModel: DynamicRouteViewModel by viewModel()
    private lateinit var carpoolViewModel: CarpoolViewModel

    private var listDeparturePoints = arrayListOf<Point>()
    private var listDestinationPoints = arrayListOf<Point>()

    private var latDeparture: Double? = null
    private var lonDeparture: Double? = null
    private var latDestination: Double? = null
    private var lonDestination: Double? = null

    private lateinit var loadingDialog: AlertDialog

    private var carpoolId: Int = -1
    private var carpoolCapacity: Int = -1

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                }
            }
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
        _binding = FragmentBottomSheetApplyCarpoolBinding.inflate(inflater, container, false)
        val inflaterLoading: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoading.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        carpoolViewModel =
            (requireActivity() as CarpoolActivity).carpoolViewModel

        carpoolId = this.requireArguments().getInt(KEY_CARPOOL_ID)
        carpoolCapacity = this.requireArguments().getInt(KEY_CAPACITY)

        binding.edCapacity.apply {
            hint = StringBuilder("Jumlah Penumpang (Max. $carpoolCapacity)")
            addTextChangedListener { capacity ->
                if (capacity!!.isNotEmpty()) {
                    if (capacity.toString().toInt() > carpoolCapacity) {
                        setText("")
                        alertDialogMessage(
                            requireContext(),
                            "Jumlah Penumpang maksimal adalah $carpoolCapacity!",
                            "Peringatan"
                        )
                    }
                }
            }
        }

        setTransparentBackground()
        setupBackPressListener()

        setPlacesAutocomplete()
        observePlacesAutoComplete()

        setupPlayAnimation()
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
                    setMessage("Batalkan sesi Pengajuan Penawaran Carpool?")
                    setPositiveButton("Ya") { _, _ ->
                        dismiss()
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }

            switchCurrentLocation.setOnCheckedChangeListener { compoundButton, _ ->
                if (compoundButton.isChecked) {
                    edDeparturePlace.isEnabled = false
                    getMyLastLocation()
                } else {
                    edDeparturePlace.isEnabled = true
                    edDeparturePlace.setText("")
                    latDeparture = null
                    lonDeparture = null
                }
            }

            btnStartDeparture.setOnClickListener {
                if (edDeparturePlace.text.toString().isEmpty()) {
                    alertDialogMessage(
                        requireContext(),
                        "Isi lokasi keberangkatan terlebih dahulu!"
                    )
                } else {
                    dynamicRouteViewModel.searchPlaces(edDeparturePlace.text.toString())
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    val placesItem = result.data.hits

                                    if (placesItem!!.isEmpty()) {
                                        alertDialogMessage(
                                            requireContext(),
                                            "Hasil lokasi kosong!"
                                        )
                                    } else {
                                        val placesName = arrayListOf<String>()
                                        listDeparturePoints.clear()

                                        for (places in placesItem!!) {
                                            var tempPlace = "${places?.name}"
                                            if (places?.city != null) {
                                                tempPlace += ", ${places.city}"
                                            }

                                            if (places?.country != null) {
                                                tempPlace += ", ${places.country}"
                                            }
                                            placesName.add(tempPlace)
                                            listDeparturePoints.add(places?.point!!)
                                        }

                                        val distinctedPlaces = placesName.distinct()
                                        val adapter = ArrayAdapter(
                                            requireContext(),
                                            R.layout.custom_spinner_row,
                                            distinctedPlaces
                                        )
                                        adapter.notifyDataSetChanged()
                                        binding.edDeparturePlace.setAdapter(adapter)
                                        binding.edDeparturePlace.showDropDown()
                                    }
                                }

                                is Result.Error -> {
                                    loadingDialog.dismiss()

                                }
                            }
                        }
                }
            }

            btnStartDestination.setOnClickListener {
                if (edDestinationPlace.text.toString().isEmpty()) {
                    alertDialogMessage(
                        requireContext(),
                        "Isi lokasi keberangkatan terlebih dahulu!"
                    )
                } else {
                    dynamicRouteViewModel.searchPlaces(edDestinationPlace.text.toString())
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    val placesItem = result.data.hits

                                    if (placesItem!!.isEmpty()) {
                                        alertDialogMessage(
                                            requireContext(),
                                            "Hasil lokasi kosong!"
                                        )
                                    } else {
                                        val placesName = arrayListOf<String>()
                                        listDestinationPoints.clear()

                                        for (places in placesItem!!) {
                                            var tempPlace = "${places?.name}"
                                            if (places?.city != null) {
                                                tempPlace += ", ${places.city}"
                                            }


                                            if (places?.country != null) {
                                                tempPlace += ", ${places.country}"
                                            }
                                            placesName.add(tempPlace)
                                            listDestinationPoints.add(places?.point!!)
                                        }

                                        val distinctedPlaces = placesName.distinct()
                                        val adapter = ArrayAdapter(
                                            requireContext(),
                                            R.layout.custom_spinner_row,
                                            distinctedPlaces
                                        )
                                        adapter.notifyDataSetChanged()
                                        binding.edDestinationPlace.setAdapter(adapter)
                                        binding.edDestinationPlace.showDropDown()
                                    }
                                }

                                is Result.Error -> {
                                    loadingDialog.dismiss()

                                }
                            }
                        }
                }
            }


            edDeparturePlace.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, idx, _ ->
                    latDeparture = listDeparturePoints[idx].lat
                    lonDeparture = listDeparturePoints[idx].lng
                }

            edDestinationPlace.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, idx, _ ->
                    latDestination = listDestinationPoints[idx].lat
                    lonDestination = listDestinationPoints[idx].lng
                }

            btnSave.setOnClickListener {
                if (isValid()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Peringatan")
                        setMessage("Apakah lokasi penjemputan dan tujuan sudah benar?")
                        setPositiveButton("Ya") { infoDialog, _ ->
                            carpoolViewModel.applyPassenger(
                                carpoolId,
                                latDeparture,
                                lonDeparture,
                                latDestination,
                                lonDestination,
                                binding.edDeparturePlace.text.toString(),
                                binding.edDestinationPlace.text.toString(),
                                binding.edCapacity.text.toString().toInt()
                            ).observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        loadingDialog.show()
                                    }

                                    is Result.Success -> {
                                        loadingDialog.dismiss()
                                        if (result.data.success!!) {
                                            val builderInfo =
                                                AlertDialog.Builder(requireContext())
                                            builderInfo.setCancelable(false)

                                            with(builderInfo)
                                            {
                                                setTitle("Sukses Mengajukan Tawaran Carpool")
                                                setMessage("Klik OK untuk melanjutkan.")
                                                setPositiveButton("OK") { infoDialog, _ ->
                                                    carpoolViewModel.getCarpoolingData()
                                                    infoDialog.dismiss()
                                                    dismiss()
                                                }
                                                show()
                                            }
                                        } else {
                                            alertDialogMessage(
                                                requireContext(),
                                                result.data.message.toString(),
                                                "Gagal"
                                            )
                                        }
                                    }

                                    is Result.Error -> {
                                        loadingDialog.dismiss()
                                        alertDialogMessage(
                                            requireContext(),
                                            "Anda sudah pernah apply carpool ini!",
                                            "Gagal Apply"
                                        )
                                    }
                                }
                            }
                            infoDialog.dismiss()
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

    private fun setPlacesAutocomplete() {
        binding.apply {
            edDeparturePlace.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    lifecycleScope.launch {
                        latDeparture = null
                        lonDeparture = null
                        edDeparturePlaceLayout.error = null
//                        dynamicRouteViewModel.queryChannelDeparture.value = p0.toString()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            edDestinationPlace.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    lifecycleScope.launch {
                        latDestination = null
                        lonDestination = null
                        edDestinationPlaceLayout.error = null
//                        dynamicRouteViewModel.queryChannelDestination.value = p0.toString()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }
    }

    private fun observePlacesAutoComplete() {
//        dynamicRouteViewModel.apply {
//            searchPlacesResultDeparture.observe(this@BottomSheetApplyCarpool) { placesItem ->
//                val placesName = arrayListOf<String>()
//                listDeparturePoints.clear()
//
//                for (places in placesItem!!) {
//                    var tempPlace = "${places?.name}"
//                    if (places?.city != null) {
//                        tempPlace += ", ${places.city}"
//                    }
//
//                    if (places?.country != null) {
//                        tempPlace += ", ${places.country}"
//                    }
//                    placesName.add(tempPlace)
//                    listDeparturePoints.add(places?.point!!)
//                }
//                val distinctedPlaces = placesName.distinct()
//                val adapter = ArrayAdapter(
//                    requireContext(),
//                    R.layout.custom_spinner_row,
//                    distinctedPlaces
//                )
//                adapter.notifyDataSetChanged()
//                binding.edDeparturePlace.setAdapter(adapter)
//            }
//
//            searchPlacesResultDestination.observe(this@BottomSheetApplyCarpool) { placesItem ->
//                val placesName = arrayListOf<String>()
//                listDestinationPoints.clear()
//
//                for (places in placesItem!!) {
//                    var tempPlace = "${places?.name}"
//                    if (places?.city != null) {
//                        tempPlace += ", ${places.city}"
//                    }
//
//                    if (places?.country != null) {
//                        tempPlace += ", ${places.country}"
//                    }
//                    placesName.add(tempPlace)
//                    listDestinationPoints.add(places?.point!!)
//                }
//                val distinctedPlaces = placesName.distinct()
//                val adapter = ArrayAdapter(
//                    requireContext(),
//                    R.layout.custom_spinner_row,
//                    distinctedPlaces
//                )
//                adapter.notifyDataSetChanged()
//                binding.edDestinationPlace.setAdapter(adapter)
//            }
//        }
    }

    private fun isValid(): Boolean {
        return if (latDeparture == null || lonDeparture == null) {
            binding.edDeparturePlace.error = "Pilih Lokasi dari pilihan yang ada!"
            false
        } else if (latDestination == null || lonDestination == null) {
            binding.edDestinationPlace.error = "Pilih Lokasi dari pilihan yang ada!"
            false
        } else if (binding.edCapacity.text.toString()
                .isEmpty() || binding.edCapacity.text.toString().toInt() < 1
        ) {
            alertDialogMessage(requireContext(), "Masukkan Jumlah Penumpang dengan benar!")
            false
        } else {
            true
        }
    }

    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val departureLayout: Animator =
            ObjectAnimator.ofFloat(binding.departureLayout, View.ALPHA, 1f).setDuration(200)
        val destinationLayout: Animator =
            ObjectAnimator.ofFloat(binding.destinationLayout, View.ALPHA, 1f).setDuration(200)
        val informationLayout: Animator =
            ObjectAnimator.ofFloat(binding.informationLayout, View.ALPHA, 1f).setDuration(200)
        val button: Animator =
            ObjectAnimator.ofFloat(binding.btnSave, View.ALPHA, 1f).setDuration(200)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(departureLayout, destinationLayout, informationLayout, button)
        animatorSet.startDelay = 200
        animatorSet.start()
    }

    private fun setupBackPressListener() {
        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    location.apply {
                        binding.edDeparturePlace.setText(
                            Geocoder(requireContext()).getFromLocation(
                                latitude,
                                longitude, 1
                            )
                                ?.get(0)!!
                                .getAddressLine(0)
                        )
                        latDeparture = latitude
                        lonDeparture = longitude
                    }
                } else {
                    alertDialogMessage(
                        requireContext(),
                        "Lokasi tidak ditemukan",
                        "Gagal mengambil lokasi"
                    )
                    binding.edDeparturePlace.setText(StringBuilder("Lokasi tidak ditemukan."))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(
        requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val KEY_CAPACITY = "key_capacity"
    }
}