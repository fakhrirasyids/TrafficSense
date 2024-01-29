package com.lazycode.trafficsense.ui.dynamicrouting

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.PathsItem
import com.lazycode.trafficsense.databinding.ActivityDynamicRoutingBinding
import com.lazycode.trafficsense.ui.dynamicrouting.picklocation.PickLocationActivity
import com.lazycode.trafficsense.ui.dynamicrouting.picklocation.PickLocationActivity.Companion.KEY_CHOICE
import com.lazycode.trafficsense.ui.dynamicrouting.picklocation.PickLocationActivity.Companion.KEY_DEPARTURE
import com.lazycode.trafficsense.ui.dynamicrouting.picklocation.PickLocationActivity.Companion.KEY_DESTINATION
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import org.koin.android.viewmodel.ext.android.viewModel


class DynamicRoutingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDynamicRoutingBinding

    private val dynamicRoutingViewModel: DynamicRoutingViewModel by viewModel()
    private var allLatLng = ArrayList<LatLng>()

    private var isPickedDeparture: Boolean? = null
    private var isPickedDestination: Boolean? = null

    private lateinit var loadingDialog: AlertDialog

    private val getDepartureMapResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.let { res ->
                isPickedDeparture = res.getBooleanExtra(Constants.Location.isPicked.name, false)
                dynamicRoutingViewModel.isPickedDeparture.postValue(isPickedDeparture)
                val latitude = res.getDoubleExtra(
                    Constants.Location.Latitude.name,
                    0.0
                )
                val longitude = res.getDoubleExtra(
                    Constants.Location.Longitude.name,
                    0.0
                )

                binding.tvLocationPickedDeparture.text =
                    Constants.parseAddress(this, latitude, longitude)
                dynamicRoutingViewModel.departureLat.postValue(latitude)
                dynamicRoutingViewModel.departureLon.postValue(longitude)
            }
        }
    }

    private val getDestinationMapResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.let { res ->
                isPickedDestination = res.getBooleanExtra(Constants.Location.isPicked.name, false)
                dynamicRoutingViewModel.isPickedDestination.postValue(isPickedDestination)
                val latitude = res.getDoubleExtra(
                    Constants.Location.Latitude.name,
                    0.0
                )
                val longitude = res.getDoubleExtra(
                    Constants.Location.Longitude.name,
                    0.0
                )

                binding.tvLocationPickedDestination.text =
                    Constants.parseAddress(this, latitude, longitude)
                dynamicRoutingViewModel.destinationLat.postValue(latitude)
                dynamicRoutingViewModel.destinationLon.postValue(longitude)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicRoutingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.INDONESIA_LATLNG, 15f))

        observePickedLocation()
        observePathsItem()

        setListeners()
    }

    private fun observePickedLocation() {
        dynamicRoutingViewModel.apply {
            isPickedDeparture.observe(this@DynamicRoutingActivity) {
                binding.apply {
                    layoutLocationDeparture.isVisible = !it
                    layoutShowLocationDeparture.isVisible = it
                }
            }

            isPickedDestination.observe(this@DynamicRoutingActivity) {
                binding.apply {
                    layoutLocationDestination.isVisible = !it
                    layoutShowLocationDestination.isVisible = it
                }
            }

            departureLat.observe(this@DynamicRoutingActivity) { lat ->
                departureLon.observe(this@DynamicRoutingActivity) { lon ->
                    if (lat != null && lon != null) {
                        binding.tvLocationPickedDeparture.text =
                            Constants.parseAddress(this@DynamicRoutingActivity, lat, lon)
                    }
                }
            }

            destinationLat.observe(this@DynamicRoutingActivity) { lat ->
                destinationLon.observe(this@DynamicRoutingActivity) { lon ->
                    if (lat != null && lon != null) {
                        binding.tvLocationPickedDestination.text =
                            Constants.parseAddress(this@DynamicRoutingActivity, lat, lon)
                    }
                }
            }
        }
    }

    private fun observePathsItem() {
        dynamicRoutingViewModel.pathsList.observe(this@DynamicRoutingActivity) { pathsItemList ->
            if (pathsItemList.isNotEmpty() && pathsItemList != null) {
                var counter = 0
                val nodeFirst = pathsItemList[0].points?.coordinates!![0]!!
                val nodeLast = pathsItemList[0].points?.coordinates!!.last()

                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            nodeFirst[1],
                            nodeFirst[0]
                        ), 16f
                    )
                )

                mMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(
                            LatLng(nodeFirst[1], nodeFirst[0])
                        )
                )?.showInfoWindow()

                mMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(
                            LatLng(nodeLast!![1], nodeLast[0])
                        )
                )?.showInfoWindow()

                for (paths in pathsItemList) {
                    allLatLng.clear()

                    for (node in paths.points?.coordinates!!) {
                        val pos = LatLng(node!![1], node[0])
                        allLatLng.add(pos)
                    }

                    mMap.addPolyline(
                        PolylineOptions()
                            .color(COLOR_LIST[counter])
                            .width(10f)
                            .addAll(allLatLng)
                    )

                    counter++
                    if (counter == 2) {
                        break
                    }
                }

                binding.btnInfo.isVisible = true
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }

            layoutLocationDeparture.setOnClickListener {
                val iPickDeparture =
                    Intent(this@DynamicRoutingActivity, PickLocationActivity::class.java)
                iPickDeparture.putExtra(KEY_CHOICE, KEY_DEPARTURE)
                getDepartureMapResult.launch(iPickDeparture)
            }

            layoutLocationDestination.setOnClickListener {
                val iPickDestination =
                    Intent(this@DynamicRoutingActivity, PickLocationActivity::class.java)
                iPickDestination.putExtra(KEY_CHOICE, KEY_DESTINATION)
                getDestinationMapResult.launch(iPickDestination)
            }

            btnClearLocationDeparture.setOnClickListener {
                dynamicRoutingViewModel.isPickedDeparture.postValue(false)
            }

            btnClearLocationDestination.setOnClickListener {
                dynamicRoutingViewModel.isPickedDestination.postValue(false)
            }

            btnStart.setOnClickListener {
                if (!dynamicRoutingViewModel.isPickedDeparture.value!! || !dynamicRoutingViewModel.isPickedDeparture.value!!) {
                    alertDialogMessage(
                        this@DynamicRoutingActivity,
                        "Pilih Lokasi Keberangkatan/Tujuan dengan benar!",
                        "Peringatan"
                    )
                } else {
                    dynamicRoutingViewModel.navigateDestination()
                        .observe(this@DynamicRoutingActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()
                                    dynamicRoutingViewModel.pathsList.postValue(result.data.payload?.paths as ArrayList<PathsItem>?)
//                                    alertDialogMessage(this@DynamicRoutingActivity, result.data.toString())
                                }

                                is Result.Error -> {
                                    dynamicRoutingViewModel.navigateDestination()
                                        .observe(this@DynamicRoutingActivity) { result ->
                                            when (result) {
                                                is Result.Loading -> {
                                                    loadingDialog.show()
                                                }

                                                is Result.Success -> {
                                                    loadingDialog.dismiss()
                                                    dynamicRoutingViewModel.pathsList.postValue(result.data.payload?.paths as ArrayList<PathsItem>?)
//                                    alertDialogMessage(this@DynamicRoutingActivity, result.data.toString())
                                                }

                                                is Result.Error -> {
                                    loadingDialog.dismiss()
                                    alertDialogMessage(
                                        this@DynamicRoutingActivity,
                                        result.error,
                                        "Error"
                                    )
                                                }
                                            }
                                        }
//                                    loadingDialog.dismiss()
//                                    alertDialogMessage(
//                                        this@DynamicRoutingActivity,
//                                        result.error,
//                                        "Error"
//                                    )
                                }
                            }
                        }
                }
            }
        }
    }

    companion object {
        val COLOR_LIST = listOf(
            Color.BLUE,
            Color.GREEN,
            Color.GRAY
        )
    }
}