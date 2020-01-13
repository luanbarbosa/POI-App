package com.luanbarbosagomes.poiapp.feature.navigation

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.provider.poi.Poi

class ActivityNavigation : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        App.daggerMainComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        readIntentData()
    }

    private fun readIntentData() {
        val currentLocation = intent.getParcelableExtra<Location>(CURRENT_LOCATION)
        val poi = Gson().fromJson(intent.getStringExtra(POI), Poi::class.java)
    }


    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap?.apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@ActivityNavigation,
                    R.raw.google_maps_style
                )
            )
        }
    }

    companion object {
        const val CURRENT_LOCATION = "currentLocation"
        const val POI = "poi"
    }
}
