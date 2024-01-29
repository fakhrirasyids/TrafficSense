package com.lazycode.trafficsense.ui.main.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentScanBinding
import com.lazycode.trafficsense.utils.Constants.INDONESIA_LATLNG
import com.lazycode.trafficsense.utils.Constants.getOverlayBitmap
import org.koin.android.viewmodel.ext.android.viewModel

class ScanFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private val scanViewModel: ScanViewModel by viewModel()

    private val circleMapList: ArrayList<Circle> = arrayListOf()
    private val groundOverlayList: ArrayList<GroundOverlay> = arrayListOf()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        mMap.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isIndoorLevelPickerEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = true
            }
        }

        observeSensors()
    }

    private fun observeSensors() {
        scanViewModel.sensorList.observe(viewLifecycleOwner) { listSensor ->
            for (circle in circleMapList) {
                circle.remove()
            }
            circleMapList.clear()

            for (groundOverlay in groundOverlayList) {
                groundOverlay.remove()
            }
            groundOverlayList.clear()

            for (sensor in listSensor) {
                val sensorLoc = LatLng(sensor.latitude!!.toDouble(), sensor.longitude!!.toDouble())
                val fillColor = if (sensor.carbonMonoxide!! > 100) {
                    0x22FF0000
                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
                    0x22FFFF00
                } else {
                    0x22008000
                }

                val strokeColor = if (sensor.carbonMonoxide!! > 100) {
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
                    ContextCompat.getColor(requireContext(), R.color.yellow)
                } else {
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                }

//                var textColor = if (sensor.carbonMonoxide!! > 100) {
//                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
//                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
//                    ContextCompat.getColor(requireContext(), R.color.yellow)
//                } else {
//                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
//                }

                val overlayOptions = GroundOverlayOptions()
                    .position(sensorLoc, 100f)
                    .image(
                        BitmapDescriptorFactory.fromBitmap(
                            getOverlayBitmap(
                                "CO : ${sensor.carbonMonoxide}",
                                strokeColor
                            )
                        )
                    )


                val circle = mMap.addCircle(
                    CircleOptions()
                        .center(sensorLoc)
                        .radius(sensor.radius!!.toDouble())
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .strokeWidth(3f)
                )

                val groundOverlay = mMap.addGroundOverlay(overlayOptions)

                circleMapList.add(
                    circle
                )

                groundOverlayList.add(
                    groundOverlay!!
                )
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 16f))
                        mMap.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(
                                    LatLng(location.latitude, location.longitude)
                                )
                        )?.showInfoWindow()
                    }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDONESIA_LATLNG, 15f))
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

    override fun onStop() {
        scanViewModel.pauseJob()
        super.onStop()
    }

    override fun onStart() {
        scanViewModel.getSensor()
        super.onStart()
    }
}