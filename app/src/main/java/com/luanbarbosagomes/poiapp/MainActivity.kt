package com.luanbarbosagomes.poiapp

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var locationProvider: FusedLocationProviderClient

    private val hasLocationPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        requestLocationPermission()
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
                locationPermissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionCode && grantResults.firstOrNull() == PERMISSION_GRANTED) {
            setupLocationUpdate()
        }
    }

    private fun setupLocationUpdate() {
        // TODO - setup and observe location changes
    }

    companion object {
        private const val locationPermissionCode = 111
    }
}
