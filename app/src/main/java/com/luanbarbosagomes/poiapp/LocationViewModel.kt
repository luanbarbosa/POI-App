package com.luanbarbosagomes.poiapp

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import io.reactivex.*
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

val Location.latLong: LatLng
    get() = LatLng(latitude, longitude)

/**
 * [ViewModel] responsible for location based operations, such as location update emission.
 */
class LocationViewModel @Inject constructor() : ViewModel() {

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 2000
        fastestInterval = 1000
        smallestDisplacement = 10f
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun locationObservable(context: Context): Flowable<Location> =
        PublishSubject
            .create<Location> { emitter -> listenForLocation(context, emitter) }
            .toFlowable(BackpressureStrategy.LATEST)

    /**
     * TL;DR Callback to Rx observable conversion aid.
     *
     * Setup the location update service listener and emit each result of [LocationCallback]
     * using [ObservableEmitter].
     */
    private fun listenForLocation(
        context: Context,
        emitter: ObservableEmitter<Location>
    ) {
        LocationServices
            .getFusedLocationProviderClient(context)
            .requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult?) {
                        result?.lastLocation?.let {
                            emitter.onNext(it)
                        }
                    }
                },
                Looper.getMainLooper()
            )
    }

}
