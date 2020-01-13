package com.luanbarbosagomes.poiapp.provider.location

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.luanbarbosagomes.poiapp.App
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

val Location.latLong: LatLng
    get() = LatLng(latitude, longitude)

/**
 * [ViewModel] responsible for operations related to location, such as location update handling.
 */
@Singleton
class LocationViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var locationProvider: LocationProvider

    private val locationSubject: PublishSubject<Location?> = PublishSubject.create()

    var lastLocation: Location? = null

    init {
        App.daggerMainComponent.inject(this)
    }

    fun locationObservable(): Observable<Location?> =
        locationSubject
            .doOnSubscribe {
                locationProvider
                    .locationObservable(LocationProvider.LocationRequestOptions.ignoreSmallChanges)
                    .doOnNext { currentLocation ->
                        if (lastLocation != currentLocation) {
                            lastLocation = currentLocation
                            locationSubject.onNext(currentLocation)
                        }
                    }.subscribe()
            }

}
