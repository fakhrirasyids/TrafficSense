package com.lazycode.trafficsense.ui.dynamicroute.pickroute

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.lazycode.trafficsense.data.models.NavigatorPayload
import com.lazycode.trafficsense.data.models.PathsItem
import com.lazycode.trafficsense.data.models.SensorItemNavigator
import com.lazycode.trafficsense.data.models.SensorsItemPayload
import com.lazycode.trafficsense.data.models.StartPayload
import com.lazycode.trafficsense.databinding.ActivityAvailableRoutesBinding
import com.lazycode.trafficsense.ui.adapter.NavigateRouteAdapter
import com.lazycode.trafficsense.ui.dynamicroute.savedroute.SavedRouteActivity
import com.lazycode.trafficsense.ui.main.ui.scan.ScanViewModel
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Constants.getOverlayBitmap
import com.lazycode.trafficsense.utils.Constants.milesToKilometers
import com.lazycode.trafficsense.utils.Result
import de.p72b.maps.animation.AnimatedPolyline
import kotlinx.parcelize.RawValue
import org.koin.android.viewmodel.ext.android.viewModel

class AvailableRoutesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAvailableRoutesBinding
    private var allLatLng = ArrayList<LatLng>()
    private var animatedPolyline: AnimatedPolyline? = null

    private val availableRoutesViewModel: AvailableRoutesViewModel by viewModel()

    private val circleMapList: ArrayList<Circle> = arrayListOf()
    private val groundOverlayList: ArrayList<GroundOverlay> = arrayListOf()
    private lateinit var loadingDialog: AlertDialog

    private var pickedRoute: Int = 0
    private var selectedRoute: Int = 0

    private val departureInfo by lazy { intent.getStringExtra(KEY_DEPARTURE_INFO) }
    private val arriveInfo by lazy { intent.getStringExtra(KEY_ARRIVE_INFO) }

    private val navigatorResponse: NavigatorPayload by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_NAVIGATOR_RESPONSE)!!
        } else {
            intent.getParcelableExtra(KEY_NAVIGATOR_RESPONSE, NavigatorPayload::class.java)!!
        }
    }

    private val navigateRouteAdapter = NavigateRouteAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvailableRoutesBinding.inflate(layoutInflater)
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

        setToolbars()
        observeSensors()

        setMarkers()
        setRecyclerView()
        initRoute(0)

        setListeners()
    }

    private fun setToolbars() {
        binding.toolbar.apply {
            setNavigationOnClickListener { finish() }
            inflateMenu(R.menu.route_menu)
            setOnMenuItemClickListener { id ->
                if (id.itemId == R.id.menu_routes) {
                    val iSavedRouted =
                        Intent(this@AvailableRoutesActivity, SavedRouteActivity::class.java)
                    startActivity(iSavedRouted)
                }
                true
            }
        }
    }

    private fun setMarkers() {
        val selectedRoute = navigatorResponse.paths!![0]!!
        val nodeFirst = selectedRoute.points?.coordinates!![0]!!
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

    private fun setRecyclerView() {
        binding.rvRouteLayout.apply {
            navigateRouteAdapter.setList(navigatorResponse.paths as ArrayList<PathsItem>)
            navigateRouteAdapter.onRouteClick = { pathsItem, position ->
                navigateRouteAdapter.changeBtnBehaviour(position)
                binding.btnSave.text = StringBuilder("Simpan Rute ${position + 1}")
                pickedRoute = position

                initRoute(position)
            }

            adapter = navigateRouteAdapter
            layoutManager = LinearLayoutManager(
                this@AvailableRoutesActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun initRoute(index: Int) {
        selectedRoute = index

        if (animatedPolyline != null) {
            animatedPolyline?.remove()
        }

        val selectedRoute = navigatorResponse.paths!![index]!!

        binding.apply {
            tvInfoRoute.text =
                StringBuilder("Info Rute ${index + 1}${if (index == 0) " (Terbaik)" else ""}")
            tvCoInfo.text = selectedRoute.co.toString()
            tvDistanceInfo.text = StringBuilder(
                "${
                    milesToKilometers(
                        selectedRoute.distance.toString().toDouble()
                    )
                } Km"
            )
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
        availableRoutesViewModel.sensorList.observe(this@AvailableRoutesActivity) { listSensor ->
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
                    LatLng(sensor.latitude!!.toDouble(), sensor?.longitude!!.toDouble())

                val fillColor = if (sensor.carbonMonoxide!! > 100) {
                    0x22FF0000
                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
                    0x22FFFF00
                } else {
                    0x22008000
                }

                val strokeColor = if (sensor.carbonMonoxide > 100) {
                    ContextCompat.getColor(this@AvailableRoutesActivity, android.R.color.holo_red_dark)
                } else if (sensor.carbonMonoxide > 50 && sensor.carbonMonoxide <= 100) {
                    ContextCompat.getColor(this@AvailableRoutesActivity, R.color.yellow)
                } else {
                    ContextCompat.getColor(this@AvailableRoutesActivity, android.R.color.holo_green_dark)
                }

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

    private fun setListeners() {
        binding.apply {
            btnSave.setOnClickListener {
                val builder = AlertDialog.Builder(this@AvailableRoutesActivity)
                builder.setCancelable(false)

                with(builder)
                {
                    setTitle("Peringatan")
                    setMessage("Apakah anda ingin menyimpan Rute ${pickedRoute + 1}?")
                    setPositiveButton("Ya") { dialog, _ ->
                        val navigatorTemp = navigatorResponse.paths!![selectedRoute]!!
                        val sensorsItemPayloadList = arrayListOf<SensorsItemPayload>()
                        val sensorItemNavigator = navigatorTemp.sensors

                        for (sensor in sensorItemNavigator!!) {
                            sensorsItemPayloadList.add(
                                SensorsItemPayload(
                                    id = sensor?.id.toString().toInt()
                                )
                            )
                        }

                        val startPayload = StartPayload(
                            distance = navigatorTemp.distance,
                            bbox = navigatorTemp.bbox,
                            points = navigatorTemp.points,
                            carbonMonoxide = navigatorTemp.co,
                            sensors = sensorsItemPayloadList,
                            departureInfo = departureInfo,
                            arriveInfo = arriveInfo
                        )

                        dialog.dismiss()
                        availableRoutesViewModel.startRoute(startPayload)
                            .observe(this@AvailableRoutesActivity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        loadingDialog.show()
                                    }

                                    is Result.Success -> {
                                        loadingDialog.dismiss()
                                        if (result.data.success!!) {
                                            alertDialogMessage(
                                                this@AvailableRoutesActivity,
                                                "Sukses menyimpan Rute",
                                                "Sukses"
                                            )
                                        } else {
                                            alertDialogMessage(
                                                this@AvailableRoutesActivity,
                                                result.data.message.toString(),
                                                "Gagal"
                                            )
                                        }
                                    }

                                    is Result.Error -> {
                                        loadingDialog.dismiss()
                                        alertDialogMessage(
                                            this@AvailableRoutesActivity,
                                            result.error,
                                            "Gagal menyimpan Rute"
                                        )
                                    }
                                }
                            }
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        }
    }

    companion object {
        const val KEY_NAVIGATOR_RESPONSE = "key_navigator_response"
        const val KEY_DEPARTURE_INFO = "key_departure_info"
        const val KEY_ARRIVE_INFO = "key_arrive_info"
    }
}