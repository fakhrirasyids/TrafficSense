package com.lazycode.trafficsense.ui.carpool.addcarpool

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.Point
import com.lazycode.trafficsense.databinding.ActivityAddCarpoolBinding
import com.lazycode.trafficsense.ui.carpool.vehicle.VehicleViewModel
import com.lazycode.trafficsense.ui.dynamicroute.DynamicRouteViewModel
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCarpoolActivity : AppCompatActivity(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityAddCarpoolBinding
    private val vehicleViewModel: VehicleViewModel by viewModel()
    private val dynamicRouteViewModel: DynamicRouteViewModel by viewModel()
    private val addCarpoolViewModel: AddCarpoolViewModel by viewModel()

    private var listDeparturePoints = arrayListOf<Point>()
    private var listDestinationPoints = arrayListOf<Point>()

    private var latDeparture: Double? = null
    private var lonDeparture: Double? = null
    private var latDestination: Double? = null
    private var lonDestination: Double? = null

    private lateinit var selectedCalendar: Calendar
    private lateinit var loadingDialog: AlertDialog

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCarpoolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()
        selectedCalendar = Calendar.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setVehicles()
        setPlacesAutocomplete()
        observePlacesAutoComplete()

        setupPlayAnimation()

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }

            edDate.setOnClickListener {
                showDatePicker()
            }

            switchCurrentLocation.setOnCheckedChangeListener { compoundButton, _ ->
                if (compoundButton.isChecked) {
                    edDeparturePlace.isEnabled = false
                    btnStartDeparture.isEnabled = false
                    getMyLastLocation()
                } else {
                    edDeparturePlace.isEnabled = true
                    btnStartDeparture.isEnabled = true
                    edDeparturePlace.setText("")
                    latDeparture = null
                    lonDeparture = null
                }
            }

            btnStartDeparture.setOnClickListener {
                if (edDeparturePlace.text.toString().isEmpty()) {
                    alertDialogMessage(
                        this@AddCarpoolActivity,
                        "Isi lokasi keberangkatan terlebih dahulu!"
                    )
                } else {
                    dynamicRouteViewModel.searchPlaces(edDeparturePlace.text.toString())
                        .observe(this@AddCarpoolActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    val placesItem = result.data.hits

                                    if (placesItem!!.isEmpty()) {
                                        alertDialogMessage(
                                            this@AddCarpoolActivity,
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
                                            this@AddCarpoolActivity,
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
                        this@AddCarpoolActivity,
                        "Isi lokasi keberangkatan terlebih dahulu!"
                    )
                } else {
                    dynamicRouteViewModel.searchPlaces(edDestinationPlace.text.toString())
                        .observe(this@AddCarpoolActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    val placesItem = result.data.hits

                                    if (placesItem!!.isEmpty()) {
                                        alertDialogMessage(
                                            this@AddCarpoolActivity,
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
                                            this@AddCarpoolActivity,
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
                    val builder = AlertDialog.Builder(this@AddCarpoolActivity)
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Peringatan")
                        setMessage("Apakah semua data carpool anda sudah benar?")
                        setPositiveButton("Ya") { infoDialog, _ ->
                            addCarpoolViewModel.storeCarpool(
                                latDeparture,
                                lonDeparture,
                                latDestination,
                                lonDestination,
                                binding.edCapacity.text.toString().toInt(),
                                binding.edDeparturePlace.text.toString(),
                                binding.edDestinationPlace.text.toString(),
                                binding.edDate.text.toString()
                            ).observe(this@AddCarpoolActivity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        loadingDialog.show()
                                    }

                                    is Result.Success -> {
                                        loadingDialog.dismiss()
                                        if (result.data.success!!) {
                                            val builderInfo =
                                                AlertDialog.Builder(this@AddCarpoolActivity)
                                            builderInfo.setCancelable(false)

                                            with(builderInfo)
                                            {
                                                setTitle("Sukses Menambahkan Carpool")
                                                setMessage("Klik OK untuk melanjutkan.")
                                                setPositiveButton("OK") { infoDialog, _ ->
                                                    infoDialog.dismiss()
                                                    val intent = Intent()
                                                    setResult(RESULT_OK, intent)
                                                    finish()
                                                }
                                                show()
                                            }
                                        } else {
                                            alertDialogMessage(
                                                this@AddCarpoolActivity,
                                                result.data.message.toString(),
                                                "Gagal"
                                            )
                                        }
                                    }

                                    is Result.Error -> {
                                        loadingDialog.dismiss()
                                        alertDialogMessage(
                                            this@AddCarpoolActivity,
                                            result.error,
                                            "Error"
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

    private fun showDatePicker() {
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val hour = selectedCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = selectedCalendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, this, hour, minute, true)
        timePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedCalendar.set(Calendar.YEAR, year)
        selectedCalendar.set(Calendar.MONTH, month)
        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        showTimePicker()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        selectedCalendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDateTime = dateFormat.format(selectedCalendar.time)
        binding.edDate.setText(formattedDateTime)
    }

    private fun setVehicles() {
        vehicleViewModel.isLoading.observe(this@AddCarpoolActivity) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        vehicleViewModel.vehicleList.observe(this@AddCarpoolActivity) { listVehicle ->
            if (listVehicle != null) {
                if (addCarpoolViewModel.selectedVehicleId.value != null) {
                    for (item in listVehicle) {
                        if (addCarpoolViewModel.selectedVehicleId.value == item.id) {
                            binding.edVehicle.setText(item.name)
                            binding.edCapacity.setText(item.capacity)
                        }
                    }
                } else {
                    binding.edVehicle.hint = "Pilih Kendaraan Anda"
                }

                binding.edVehicle.isEnabled = true
                val listVehicleString = arrayListOf<String>()
                for (item in listVehicle) {
                    listVehicleString.add(item.name.toString())
                }

                val vehicleRow = ArrayAdapter(
                    this@AddCarpoolActivity,
                    R.layout.custom_spinner_row,
                    listVehicleString
                )
                binding.edVehicle.apply {
                    setAdapter(vehicleRow)
                    setOnItemClickListener { _, _, position, _ ->
                        addCarpoolViewModel.selectedVehicleId.postValue(listVehicle[position].id!!)
                        binding.edCapacity.setText(listVehicle[position].capacity)
                    }
                }
            } else {
                binding.edVehicle.isEnabled = false
                binding.edVehicle.hint = "Anda tidak punya kendaraan"
            }


        }

        vehicleViewModel.errorText.observe(this@AddCarpoolActivity) {
            if (it.isNotEmpty()) {
                val builder = AlertDialog.Builder(this@AddCarpoolActivity)
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Gagal mendapatkan data Kendaraan")
                    setMessage("Apakan anda ingin memuat ulang?")
                    setPositiveButton("Ya") { infoDialog, _ ->
                        infoDialog.dismiss()
                        vehicleViewModel.getVehicles()
                    }
                    setNegativeButton("Tidak") { infoDialog, _ ->
                        infoDialog.dismiss()
                        finish()
                    }
                    show()
                }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val informationLayout: Animator =
            ObjectAnimator.ofFloat(binding.informationLayout, View.ALPHA, 1f).setDuration(200)
        val departureLayout: Animator =
            ObjectAnimator.ofFloat(binding.departureLayout, View.ALPHA, 1f).setDuration(200)
        val destinationLayout: Animator =
            ObjectAnimator.ofFloat(binding.destinationLayout, View.ALPHA, 1f).setDuration(200)
        val button: Animator =
            ObjectAnimator.ofFloat(binding.btnSave, View.ALPHA, 1f).setDuration(200)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(informationLayout, departureLayout, destinationLayout, button)
        animatorSet.startDelay = 200
        animatorSet.start()
    }

    private fun observePlacesAutoComplete() {
//        dynamicRouteViewModel.apply {
//            searchPlacesResultDeparture.observe(this@AddCarpoolActivity) { placesItem ->
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
//                    this@AddCarpoolActivity,
//                    R.layout.custom_spinner_row,
//                    distinctedPlaces
//                )
//                adapter.notifyDataSetChanged()
//                binding.edDeparturePlace.setAdapter(adapter)
//            }
//
//            searchPlacesResultDestination.observe(this@AddCarpoolActivity) { placesItem ->
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
//                    this@AddCarpoolActivity,
//                    R.layout.custom_spinner_row,
//                    distinctedPlaces
//                )
//                adapter.notifyDataSetChanged()
//                binding.edDestinationPlace.setAdapter(adapter)
//            }
//        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    location.apply {
                        binding.edDeparturePlace.setText(
                            Geocoder(this@AddCarpoolActivity).getFromLocation(
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
                        this@AddCarpoolActivity,
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

    private fun isValid(): Boolean {
        return if (addCarpoolViewModel.selectedVehicleId.value == null) {
            alertDialogMessage(
                this@AddCarpoolActivity,
                "Pilih Kendaraan anda terlebih dahulu!",
                "Peringatan"
            )
            false
        } else if (binding.edDate.text.toString().isEmpty()) {
            alertDialogMessage(
                this@AddCarpoolActivity,
                "Pilih Tanggal Keberangkatan terlebih dahulu!",
                "Peringatan"
            )
            false
        } else if (latDeparture == null || lonDeparture == null) {
            alertDialogMessage(
                this@AddCarpoolActivity,
                "Pilih Lokasi dari pilihan yang ada!",
                "Peringatan"
            )
            false
        } else if (latDestination == null || lonDestination == null) {
            alertDialogMessage(
                this@AddCarpoolActivity,
                "Pilih Lokasi dari pilihan yang ada!",
                "Peringatan"
            )
            false
        } else {
            true
        }
    }

    private fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}