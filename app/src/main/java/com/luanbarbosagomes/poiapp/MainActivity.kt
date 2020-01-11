package com.luanbarbosagomes.poiapp

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.luanbarbosagomes.poiapp.dagger.DaggerMainComponent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var locationViewModel: LocationViewModel
    @Inject
    lateinit var poiViewModel: PoiViewModel

    private var googleMap: GoogleMap? = null
    private lateinit var locationProvider: FusedLocationProviderClient

    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMainComponent.create().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
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
            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.google_maps_style))
            setPadding(10, 50, 10, 100)
            setOnMarkerClickListener { marker ->
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                false
            }
        }

        if (hasLocationPermission) {
            setupLocationUpdate()
        }
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
            .locationObservable(this)
            .subscribe { currentLocation ->
                moveToLocation(currentLocation)
                poiViewModel.fetchPoiData(currentLocation)
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
            )
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
