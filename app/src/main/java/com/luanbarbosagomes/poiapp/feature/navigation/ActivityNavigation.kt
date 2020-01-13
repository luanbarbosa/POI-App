package com.luanbarbosagomes.poiapp.feature.navigation

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.provider.location.latLong
import com.luanbarbosagomes.poiapp.provider.poi.DirectionsViewModel
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class ActivityNavigation : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    internal lateinit var directionsViewModel: DirectionsViewModel

    private val disposeBag = CompositeDisposable()
    private var googleMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private lateinit var poi: Poi

    override fun onCreate(savedInstanceState: Bundle?) {
        App.daggerMainComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        readIntentData()
    }

    private fun readIntentData() {
        currentLocation = intent.getParcelableExtra(CURRENT_LOCATION) ?: Location("").also { closeDueToError() }
        poi = Gson().fromJson(intent.getStringExtra(POI), Poi::class.java)
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

        directionsViewModel
            .getDirections(currentLocation!!.latLong, poi!!.latLng)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                Log.e("", "")
            }
            .addTo(disposeBag)
    }

    private fun closeDueToError() {
        Toast
            .makeText(this, getString(R.string.poi_navigation_load_error), Toast.LENGTH_LONG)
            .show()
        finish()
    }

    companion object {
        const val CURRENT_LOCATION = "currentLocation"
        const val POI = "poi"
    }
}
