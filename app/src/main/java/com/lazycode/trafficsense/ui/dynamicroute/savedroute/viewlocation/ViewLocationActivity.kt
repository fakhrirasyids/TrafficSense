package com.lazycode.trafficsense.ui.dynamicroute.savedroute.viewlocation

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.data.models.TripsSavedPayload
import com.lazycode.trafficsense.databinding.ActivityViewLocationBinding
import com.lazycode.trafficsense.ui.dynamicroute.pickroute.AvailableRoutesViewModel
import com.lazycode.trafficsense.ui.dynamicroute.savedroute.SavedRouteActivity
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Constants.getOverlayBitmap
import de.p72b.maps.animation.AnimatedPolyline
import org.koin.android.viewmodel.ext.android.viewModel

class ViewLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityViewLocationBinding
    private var allLatLng = ArrayList<LatLng>()
    private var animatedPolyline: AnimatedPolyline? = null

    private val availableRoutesViewModel: AvailableRoutesViewModel by viewModel()

    private val circleMapList: ArrayList<Circle> = arrayListOf()
    private val groundOverlayList: ArrayList<GroundOverlay> = arrayListOf()

    private val tripsSavedPayload: TripsSavedPayload by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_ROUTE_DETAIL)!!
        } else {
            intent.getParcelableExtra(KEY_ROUTE_DETAIL, TripsSavedPayload::class.java)!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setToolbars()

        setMarkers()
        initRoute()

        setListeners()
    }

    private fun setToolbars() {
        binding.toolbar.apply {
            setNavigationOnClickListener { finish() }
        }
    }

    private fun setMarkers() {
        val selectedRoute = tripsSavedPayload.detail
        val nodeFirst = selectedRoute?.points?.coordinates!![0]!!
        val nodeLast = selectedRoute.points.coordinates.last()

        mMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
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
    }

    private fun initRoute() {
        if (animatedPolyline != null) {
            animatedPolyline?.remove()
        }

        val selectedRoute = tripsSavedPayload.detail!!

        observeSensors()

        binding.apply {
            tvInfoRoute.text = StringBuilder("Info Rute Tersimpan")
            tvCoInfo.text = selectedRoute.carbonMonoxide.toString()
            tvDistanceInfo.text = StringBuilder("${
                Constants.milesToKilometers(
                    selectedRoute.distance.toString().toDouble()
                )
            } Km")

            tvDepartureInfo.text =selectedRoute.departureInfo
            tvArriveInfo.text = selectedRoute.arriveInfo
        }

        val bbox = listOf(
            LatLng(selectedRoute.bbox!![1]!!, selectedRoute.bbox[0]!!),
            LatLng(selectedRoute.bbox[3]!!, selectedRoute.bbox[2]!!),
        )
        val builder = LatLngBounds.Builder()
        for (latLng in bbox) {
            builder.include(latLng)
        }
        val bounds = builder.build()

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))

        allLatLng.clear()
        for (node in selectedRoute.points?.coordinates!!) {
            val pos = LatLng(node!![1], node[0])
            allLatLng.add(pos)
        }

        animatedPolyline = AnimatedPolyline(
            mMap,
            allLatLng,
            polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .width(10f)
                .jointType(JointType.ROUND)
        )

        animatedPolyline?.start()
    }

    private fun observeSensors() {
        availableRoutesViewModel.sensorList.observe(this@ViewLocationActivity) { listSensor ->
            for (circle in circleMapList) {
                circle.remove()
            }
            circleMapList.clear()

            for (groundOverlay in groundOverlayList) {
                groundOverlay.remove()
            }
            groundOverlayList.clear()

            for (sensor in listSensor) {
                val sensorLoc =
                    LatLng(sensor.latitude!!.toDouble(), sensor.longitude!!.toDouble())

                val fillColor = if (sensor.carbonMonoxide!! > 100) {
                    0x22FF0000
                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
                    0x22FFFF00
                } else {
                    0x22008000
                }

                val strokeColor = if (sensor.carbonMonoxide!! > 100) {
                    ContextCompat.getColor(this@ViewLocationActivity, android.R.color.holo_red_dark)
                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
                    ContextCompat.getColor(this@ViewLocationActivity, R.color.yellow)
                } else {
                    ContextCompat.getColor(this@ViewLocationActivity, android.R.color.holo_green_dark)
                }
                val overlayOptions = GroundOverlayOptions()
                    .position(sensorLoc, 100f)
                    .image(BitmapDescriptorFactory.fromBitmap(getOverlayBitmap("CO : ${sensor.carbonMonoxide}",strokeColor)))

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

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
        }
    }

    companion object {
        const val KEY_ROUTE_DETAIL = "key_route_detail"
    }
}