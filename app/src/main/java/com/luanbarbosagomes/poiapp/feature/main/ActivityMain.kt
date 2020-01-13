package com.luanbarbosagomes.poiapp.feature.main

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.feature.poi.details.PoiDetailsDialog
import com.luanbarbosagomes.poiapp.provider.location.LocationViewModel
import com.luanbarbosagomes.poiapp.provider.location.latLong
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class ActivityMain : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var locationViewModel: LocationViewModel
    @Inject
    lateinit var poiViewModel: PoiViewModel

    private var googleMap: GoogleMap? = null

    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        App.daggerMainComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        requestLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        setupLocationUpdate()
        setupPoiDataUpdate()
    }

    override fun onPause() {
        super.onPause()
        disposeBag.clear()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap?.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@ActivityMain, R.raw.google_maps_style))
            setOnMarkerClickListener(this@ActivityMain)
        }

        if (hasLocationPermission) {
            setupLocationUpdate()
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.apply {
            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            PoiDetailsDialog(
                this@ActivityMain,
                marker.tag as Poi
            ).show()
        }
        return false
    }

    private fun requestLocationPermission() {
        if (!hasLocationPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERM_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERM_CODE && grantResults.firstOrNull() == PERMISSION_GRANTED) {
            setupLocationUpdate()
        }
    }

    private fun setupLocationUpdate() {
        if (!hasLocationPermission) return

        googleMap?.apply {
            isMyLocationEnabled = true
            uiSettings.apply {
                isMyLocationButtonEnabled = true
                isMapToolbarEnabled = false
                isZoomControlsEnabled = true
            }
        }

        locationViewModel
            .locationObservable()
            .subscribe { currentLocation ->
                currentLocation?.let {
                    moveToLocation(it)
                    poiViewModel.fetchPoiData(it)
                }
            }
            .addTo(disposeBag)
    }

    private fun setupPoiDataUpdate() {
        poiViewModel
            .poiObservable()
            .subscribe { poiList ->
                addPoiToMap(poiList)
            }
            .addTo(disposeBag)

        poiViewModel
            .errorObservable()
            .subscribe { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
            .addTo(disposeBag)
    }

    private fun addPoiToMap(poiList: List<Poi>) {
        googleMap?.clear()
        poiList.forEach {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(it.latLng)
                    .title(it.title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )?.tag = it
        }
    }

    private fun moveToLocation(location: Location) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location.latLong, CURRENT_LOCATION_ZOOM)
        )
    }

    companion object {
        private const val LOCATION_PERM_CODE = 111
        private const val CURRENT_LOCATION_ZOOM = 16f
    }

}
