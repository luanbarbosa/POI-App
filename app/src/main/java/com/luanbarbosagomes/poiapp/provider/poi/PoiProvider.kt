package com.luanbarbosagomes.poiapp.provider.poi

import android.location.Location
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.rx.rxObject
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import javax.inject.Inject

data class Poi(
    @SerializedName("pageid")
    val pageId: Long,
    val title: String,
    val lat: Double,
    val lon: Double
) {

    val latLng: LatLng
        get() = LatLng(lat, lon)
}

data class Query(
    @SerializedName("geosearch") val poiList: List<Poi>?
)

data class PoiListResponse(
    val query: Query?
) {

    class Deserializer : ResponseDeserializable<PoiListResponse> {
        override fun deserialize(content: String): PoiListResponse? =
            Gson().fromJson(content, PoiListResponse::class.java)
    }
}

class UnableToFetchPoiListException(message: String) : Exception(message)

class PoiProvider @Inject constructor() {

    fun fetchPoi(location: Location): Single<List<Poi>> =
        Fuel.get(buildPoiUrl(location))
            .rxObject(PoiListResponse.Deserializer())
            .flatMap { result ->
                val (response, error) = result
                when (error) {
                    null -> Single.just(response?.query?.poiList ?: listOf())
                    else -> Single.error(
                        UnableToFetchPoiListException("Unable to retrieve POI!")
                    )
                }

            }

    private fun buildPoiUrl(location: Location) =
        POI_LIST_BASE_URL +
            "?action=query" +
            "&list=geosearch" +
            "&gsradius=$RADIUS" +
            "&gscoord=${location.latitude}|${location.longitude}" +
            "&gslimit=$POI_LIMIT" +
            "&format=json"

    companion object {
        private const val POI_LIST_BASE_URL = "https://en.wikipedia.org/w/api.php"
        private const val POI_LIMIT = 50
        private const val RADIUS = 10000
    }

}