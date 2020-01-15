package com.luanbarbosagomes.poiapp.provider.location

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
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

    var lastLocation: Location? = null

    fun locationObservable(): Flowable<Location> = locationProvider
        .locationObservable(LocationProvider.LocationRequestOptions.ignoreSmallChanges)
        .filter { lastLocation != it }
        .doOnNext { lastLocation = it }
}
