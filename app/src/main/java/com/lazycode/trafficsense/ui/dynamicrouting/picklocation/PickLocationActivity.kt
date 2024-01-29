package com.lazycode.trafficsense.ui.dynamicrouting.picklocation

import android.Manifest
import android.R.attr.apiKey
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.lazycode.trafficsense.BuildConfig
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityDynamicRoutingBinding
import com.lazycode.trafficsense.databinding.ActivityPickLocationBinding
import com.lazycode.trafficsense.ui.dynamicrouting.DynamicRoutingViewModel
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Constants.parseAddress
import org.koin.android.viewmodel.ext.android.viewModel


class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPickLocationBinding

    private var lat: Double? = null
    private var lon: Double? = null

    private val keyChoiceLocation by lazy { intent.getStringExtra(KEY_CHOICE) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickLocationBinding.inflate(layoutInflater)
        binding.toolbar.title = StringBuilder("Pick $keyChoiceLocation Location")
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setOnPoiClickListener { poi ->
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            poi.latLng.latitude,
                            poi.latLng.longitude
                        )
                    )
            )?.showInfoWindow()
            postLocationSelected(poi.latLng.latitude, poi.latLng.longitude)
        }

        mMap.setOnInfoWindowClickListener { marker ->
            postLocationSelected(marker.position.latitude, marker.position.longitude)
            marker.hideInfoWindow()
        }

        setListeners()
    }

    private fun postLocationSelected(latitude: Double, longitude: Double) {
        val address =
            parseAddress(
                this,
                latitude,
                longitude
            )
        binding.tvLocationPicked.text = address
        lat = latitude
        lon = longitude
    }


    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
            btnCancel.setOnClickListener { finish() }

            btnSave.setOnClickListener {
                if (lat != null && lon != null) {
                    val intent = Intent()
                    intent.putExtra(
                        Constants.Location.isPicked.name,
                        true
                    )
                    intent.putExtra(
                        Constants.Location.Latitude.name,
                        lat
                    )
                    intent.putExtra(
                        Constants.Location.Longitude.name,
                        lon
                    )
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    location.apply {
                        val userLoc = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15f))
                    }
                } else {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            Constants.INDONESIA_LATLNG,
                            12f
                        )
                    )
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

    companion object {
        const val KEY_CHOICE = "key_choice"
        const val KEY_DEPARTURE = "Departure"
        const val KEY_DESTINATION = "Destination"
    }
}