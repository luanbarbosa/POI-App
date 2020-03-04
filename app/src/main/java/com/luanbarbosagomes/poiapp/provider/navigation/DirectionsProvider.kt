package com.luanbarbosagomes.poiapp.provider.navigation

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.rx.rxObject
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provider responsible for retrieving/processing directions to POI related data.
 */
@Singleton
class DirectionsProvider @Inject constructor() {

    fun fetchDirections(origin: LatLng, destination: LatLng): Single<DirectionsResponse?> =
        Fuel.get(buildDirectionsUrl(origin, destination))
            .rxObject(DirectionsResponse.Deserializer())
            .flatMap { result ->
                val (response, error) = result
                when (error) {
                    null -> Single.just(response)
                    else -> Single.error(UnableToFetchDirectionsException())
                }
            }

    private fun buildDirectionsUrl(origin: LatLng, destination: LatLng) =
        "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=${origin.formatted()}" +
            "&destination=${destination.formatted()}" +
            "&mode=walking" +
            "&units=metric" +
            "&key=<YOUR_KEY>"

}

fun LatLng.formatted(): String = "$latitude,$longitude"

class UnableToFetchDirectionsException(
    message: String = "Unable to retrieve directions!"
) : Exception(message)
