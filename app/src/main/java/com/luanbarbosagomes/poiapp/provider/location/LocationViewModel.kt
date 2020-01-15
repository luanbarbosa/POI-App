package com.luanbarbosagomes.poiapp.provider.location

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ViewModel] responsible for operations related to location, such as location update handling.
 */
@Singleton
class LocationViewModel @Inject constructor(
    var locationProvider: LocationProvider
) : ViewModel() {

    private val locationSubject: PublishSubject<Location?> = PublishSubject.create()

    var lastLocation: Location? = null

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
