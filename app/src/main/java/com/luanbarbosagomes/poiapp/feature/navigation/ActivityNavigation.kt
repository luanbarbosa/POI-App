package com.luanbarbosagomes.poiapp.feature.navigation

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.provider.navigation.DirectionsResponse
import com.luanbarbosagomes.poiapp.provider.navigation.formatted
import com.luanbarbosagomes.poiapp.provider.location.latLong
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_navigation.*
import javax.inject.Inject

/**
 * Screen responsible for showing the UI with the directions to a specific location.
 */
class ActivityNavigation : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    internal lateinit var navigationViewModel: NavigationViewModel

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
        currentLocation =
            intent.getParcelableExtra(CURRENT_LOCATION) ?: Location("").also { closeDueToError() }
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
            setPadding(100, 0, 100, 0)
            isMyLocationEnabled = false
            uiSettings.apply {
                isMapToolbarEnabled = false
                isMyLocationButtonEnabled = false
            }
        }

        loadDirections()
    }

    private fun loadDirections() {
        navigationViewModel
            .getDirections(currentLocation.latLong, poi.latLng)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { navigationData, error ->
                when {
                    navigationData != null -> loadNavigationData(navigationData.routes.first())
                    error != null -> showError(error.message ?: "")
                }
            }
            .addTo(disposeBag)
    }

    private fun loadNavigationData(route: DirectionsResponse.Routes) {
        googleMap?.addMarker(
            MarkerOptions()
                .position(route.legs.first().startLocation.toLatLng())
                .title(getString(R.string.poi_navigation_you))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )?.showInfoWindow()
        googleMap?.addMarker(
            MarkerOptions()
                .position(route.legs.last().endLocation.toLatLng())
                .title(poi.title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        googleMap?.addPolyline(
            PolylineOptions()
                .addAll(PolyUtil.decode(route.overviewPolyline.points))
                .color(Color.WHITE)
        )
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(route.bounds.latLngBounds(), 0)
        )

        setupViews(route)
    }

    private fun setupViews(route: DirectionsResponse.Routes) {
        seeDetailBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                NavigationDetailsDialog(this@ActivityNavigation, poi, route).show()
            }
        }
        navigateBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener { startGoogleMapsNavigation() }
        }
    }

    private fun startGoogleMapsNavigation() {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${poi.latLng.formatted()}&mode=w"))
        )
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun closeDueToError() {
        showError(getString(R.string.poi_navigation_load_error))
        finish()
    }

    companion object {
        const val CURRENT_LOCATION = "currentLocation"
        const val POI = "poi"
    }
}

private fun DirectionsResponse.Bounds.latLngBounds(): LatLngBounds =
    LatLngBounds(southwest.toLatLng(), northeast.toLatLng())

private fun DirectionsResponse.LatLong.toLatLng(): LatLng = LatLng(lat, lng)
