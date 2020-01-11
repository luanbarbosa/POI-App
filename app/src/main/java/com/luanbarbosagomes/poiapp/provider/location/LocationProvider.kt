package com.luanbarbosagomes.poiapp.provider.location

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.luanbarbosagomes.poiapp.App
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor() {

    fun locationObservable(requestOptions: LocationRequestOptions): Flowable<Location> {
        return PublishSubject
            .create<Location> { emitter -> listenForLocation(requestOptions, emitter) }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    /**
     * TL;DR Callback to Rx observable conversion aid.
     *
     * Setup the location update service listener and emit each result of [LocationCallback]
     * using [ObservableEmitter].
     */
    private fun listenForLocation(
        requestOptions: LocationRequestOptions,
        emitter: ObservableEmitter<Location>
    ) {
        LocationServices
            .getFusedLocationProviderClient(App.context)
            .requestLocationUpdates(
                requestOptions.toLocationRequest(),
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

    data class LocationRequestOptions(
        var interval: Long,
        var fastestInterval: Long,
        var smallestDisplacement: Float
    ) {

        companion object {
            val ignoreSmallChanges = LocationRequestOptions(
                interval = 2000,
                fastestInterval = 1000,
                smallestDisplacement = 200f
            )
        }
    }

}

private fun LocationProvider.LocationRequestOptions.toLocationRequest(): LocationRequest =
    LocationRequest.create().apply {
        interval = this@toLocationRequest.interval
        fastestInterval = this@toLocationRequest.fastestInterval
        smallestDisplacement = this@toLocationRequest.smallestDisplacement
    }
