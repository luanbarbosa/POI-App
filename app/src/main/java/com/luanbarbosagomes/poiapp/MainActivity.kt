package com.luanbarbosagomes.poiapp

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val disposeBag = CompositeDisposable()

    private lateinit var locationViewModel: LocationViewModel

    private var googleMap: GoogleMap? = null
    private lateinit var locationProvider: FusedLocationProviderClient

    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        requestLocationPermission() 
    }

    override fun onResume() {
        super.onResume()
        setupLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        disposeBag.clear()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

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
        permissions: Array<out String>,
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
            uiSettings.isMyLocationButtonEnabled = true
        }

        locationViewModel
            .locationObservable(this)
            .subscribe { currentLocation ->
                moveToLocation(currentLocation)

                // TODO - update map and trigger POI data fetching (possibly)
            }
            .addTo(disposeBag)
    }

    private fun moveToLocation(location: Location) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location.latLong, CURRENT_LOCATION_ZOOM)
        )
    }

    companion object {
        private const val LOCATION_PERM_CODE = 111
        private const val CURRENT_LOCATION_ZOOM = 14f
    }
}
