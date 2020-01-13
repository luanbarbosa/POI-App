package com.luanbarbosagomes.poiapp.provider.poi

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.provider.direction.DirectionsProvider
import com.luanbarbosagomes.poiapp.provider.direction.DirectionsResponse
import io.reactivex.Single
import javax.inject.Inject

class DirectionsViewModel @Inject constructor() : ViewModel() {

    @Inject
    internal lateinit var directionsProvider: DirectionsProvider

    init {
        App.daggerMainComponent.inject(this)
    }

    fun getDirections(origin: LatLng, destination: LatLng): Single<DirectionsResponse?> =
        directionsProvider.fetchDirections(origin, destination)
}