package com.luanbarbosagomes.poiapp.feature.navigation

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.provider.navigation.DirectionsProvider
import com.luanbarbosagomes.poiapp.provider.navigation.DirectionsResponse
import io.reactivex.Single
import javax.inject.Inject

class NavigationViewModel @Inject constructor(
    var directionsProvider: DirectionsProvider
) : ViewModel() {

    fun getDirections(origin: LatLng, destination: LatLng): Single<DirectionsResponse?> =
        directionsProvider.fetchDirections(origin, destination)
}