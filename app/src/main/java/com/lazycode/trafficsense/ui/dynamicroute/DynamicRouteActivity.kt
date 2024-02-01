package com.lazycode.trafficsense.ui.dynamicroute

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.PathsItem
import com.lazycode.trafficsense.data.models.Point
import com.lazycode.trafficsense.databinding.ActivityDynamicRouteBinding
import com.lazycode.trafficsense.ui.dynamicroute.pickroute.AvailableRoutesActivity
import com.lazycode.trafficsense.ui.dynamicroute.pickroute.AvailableRoutesActivity.Companion.KEY_ARRIVE_INFO
import com.lazycode.trafficsense.ui.dynamicroute.pickroute.AvailableRoutesActivity.Companion.KEY_DEPARTURE_INFO
import com.lazycode.trafficsense.ui.dynamicroute.pickroute.AvailableRoutesActivity.Companion.KEY_NAVIGATOR_RESPONSE
import com.lazycode.trafficsense.ui.dynamicroute.savedroute.SavedRouteActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class DynamicRouteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDynamicRouteBinding
    private val dynamicRouteViewModel: DynamicRouteViewModel by viewModel()

    private var listDeparturePoints = arrayListOf<Point>()
    private var listDestinationPoints = arrayListOf<Point>()

    private var latDeparture: Double? = null
    private var lonDeparture: Double? = null
    private var latDestination: Double? = null
    private var lonDestination: Double? = null

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
        binding = ActivityDynamicRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(false)
        loadingDialog = loadingAlert.create()

        setupPlayAnimation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setPlacesAutocomplete()
        observePlacesAutoComplete()

        setListeners()
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
////            searchPlacesResultDeparture.observe(this@DynamicRouteActivity) { placesItem ->
////                val placesName = arrayListOf<String>()
////                listDeparturePoints.clear()
////
////                for (places in placesItem!!) {
////                    var tempPlace = "${places?.name}"
////                    if (places?.city != null) {
////                        tempPlace += ", ${places.city}"
////                    }
////
////                    if (places?.country != null) {
////                        tempPlace += ", ${places.country}"
////                    }
////                    placesName.add(tempPlace)
////                    listDeparturePoints.add(places?.point!!)
////                }
////
////                val distinctedPlaces = placesName.distinct()
////                val adapter = ArrayAdapter(
////                    this@DynamicRouteActivity,
////                    R.layout.custom_spinner_row,
////                    distinctedPlaces
////                )
////                adapter.notifyDataSetChanged()
////                binding.edDeparturePlace.setAdapter(adapter)
////            }
//
//            searchPlacesResultDestination.observe(this@DynamicRouteActivity) { placesItem ->
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
//                    this@DynamicRouteActivity,
//                    R.layout.custom_spinner_row,
//                    distinctedPlaces
//                )
//                adapter.notifyDataSetChanged()
//                binding.edDestinationPlace.setAdapter(adapter)
//                binding.edDestinationPlace.showDropDown()
//            }
//        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener { finish() }
                inflateMenu(R.menu.route_menu)
                setOnMenuItemClickListener { id ->
                    if (id.itemId == R.id.menu_routes) {
                        val iSavedRouted =
                            Intent(this@DynamicRouteActivity, SavedRouteActivity::class.java)
                        startActivity(iSavedRouted)
                    }
                    true
                }
            }

            btnStartDeparture.setOnClickListener {
                if (edDeparturePlace.text.toString().isEmpty()) {
                    alertDialogMessage(
                        this@DynamicRouteActivity,
                        "Isi lokasi keberangkatan terlebih dahulu!"
                    )
                } else {
                    dynamicRouteViewModel.searchPlaces(edDeparturePlace.text.toString())
                        .observe(this@DynamicRouteActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    val placesItem = result.data.hits

                                    if (placesItem!!.isEmpty()) {
                                        alertDialogMessage(
                                            this@DynamicRouteActivity,
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
                                            this@DynamicRouteActivity,
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
                        this@DynamicRouteActivity,
                        "Isi lokasi keberangkatan terlebih dahulu!"
                    )
                } else {
                    dynamicRouteViewModel.searchPlaces(edDestinationPlace.text.toString())
                        .observe(this@DynamicRouteActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    val placesItem = result.data.hits

                                    if (placesItem!!.isEmpty()) {
                                        alertDialogMessage(
                                            this@DynamicRouteActivity,
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
                                            this@DynamicRouteActivity,
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

            edDeparturePlace.onItemClickListener =
                OnItemClickListener { _, _, idx, _ ->
                    latDeparture = listDeparturePoints[idx].lat
                    lonDeparture = listDeparturePoints[idx].lng
                }

            edDestinationPlace.onItemClickListener =
                OnItemClickListener { _, _, idx, _ ->
                    latDestination = listDestinationPoints[idx].lat
                    lonDestination = listDestinationPoints[idx].lng
                }

            btnStart.setOnClickListener {
                if (isValid()) {
                    dynamicRouteViewModel.navigateDestination(
                        latDeparture,
                        lonDeparture,
                        latDestination,
                        lonDestination
                    ).observe(this@DynamicRouteActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {
                                loadingDialog.dismiss()
                                if (result.data.success!!) {
                                    val listNavigate = result.data.payload
                                    val sortedNavigate =
                                        listNavigate?.paths?.sortedWith(compareByDescending<PathsItem?> { it?.isSelected })
                                    listNavigate?.paths = null
                                    listNavigate?.paths = sortedNavigate

                                    val iShowRoute = Intent(
                                        this@DynamicRouteActivity,
                                        AvailableRoutesActivity::class.java
                                    )
                                    iShowRoute.putExtra(KEY_NAVIGATOR_RESPONSE, listNavigate)
                                    iShowRoute.putExtra(
                                        KEY_DEPARTURE_INFO,
                                        edDeparturePlace.text.toString()
                                    )
                                    iShowRoute.putExtra(
                                        KEY_ARRIVE_INFO,
                                        edDestinationPlace.text.toString()
                                    )
                                    startActivity(iShowRoute)
                                } else {
                                    alertDialogMessage(
                                        this@DynamicRouteActivity,
                                        result.data.message.toString(),
                                        "Gagal"
                                    )
                                }
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()
                                alertDialogMessage(
                                    this@DynamicRouteActivity,
                                    result.error,
                                    "Gagal Mendapatkan Rute"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        return if (latDeparture == null || lonDeparture == null) {
            binding.edDeparturePlace.error = "Pilih Lokasi dari pilihan yang ada!"
            false
        } else if (latDestination == null || lonDestination == null) {
            binding.edDestinationPlace.error = "Pilih Lokasi dari pilihan yang ada!"
            false
        } else {
            true
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
                            Geocoder(this@DynamicRouteActivity).getFromLocation(
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
                        this@DynamicRouteActivity,
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
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val departureLayout: Animator =
            ObjectAnimator.ofFloat(binding.departureLayout, View.ALPHA, 1f).setDuration(200)
        val destinationLayout: Animator =
            ObjectAnimator.ofFloat(binding.destinationLayout, View.ALPHA, 1f).setDuration(200)
        val button: Animator =
            ObjectAnimator.ofFloat(binding.btnStart, View.ALPHA, 1f).setDuration(200)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(departureLayout, destinationLayout, button)
        animatorSet.startDelay = 200
        animatorSet.start()
    }
}