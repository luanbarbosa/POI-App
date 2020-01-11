package com.luanbarbosagomes.poiapp.provider.location

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.luanbarbosagomes.poiapp.dagger.DaggerMainComponent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

val Location.latLong: LatLng
    get() = LatLng(latitude, longitude)

/**
 * [ViewModel] responsible for location based operations, such as location update emission.
 */
class LocationViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var locationProvider: LocationProvider

    private val locationSubject: PublishSubject<Location?> = PublishSubject.create()

    private var lastLocation: Location? = null

    init {
        DaggerMainComponent.create().inject(this)
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
