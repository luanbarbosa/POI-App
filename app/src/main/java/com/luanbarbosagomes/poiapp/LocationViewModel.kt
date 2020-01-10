package com.luanbarbosagomes.poiapp

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

data class CurrentLocation(val latitude: Double, val longitude: Double)

private fun Location.toCurrentLocation() = CurrentLocation(this.latitude, this.longitude)

class LocationViewModel : ViewModel() {

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 2000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun getCurrentLocation(context: Context): Flowable<CurrentLocation> =
        PublishSubject
            .create<CurrentLocation> { emitter -> listenForLocation(context, emitter) }
            .toFlowable(BackpressureStrategy.LATEST)
            .observeOn(Schedulers.computation())

    /**
     * Setup the location listener to emit the [CurrentLocation]
     */
    private fun listenForLocation(
        context: Context,
        emitter: ObservableEmitter<CurrentLocation>
    ) {
        LocationServices
            .getFusedLocationProviderClient(context)
            .requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult?) {
                        result?.lastLocation?.toCurrentLocation()?.let {
                            emitter.onNext(it)
                        }
                    }
                },
                Looper.getMainLooper()
            )
    }

}
