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
import javax.inject.Singleton

/**
 * Provider responsible for retrieving/processing POI related data.
 */
@Singleton
class PoiProvider @Inject constructor() {

    fun fetchPoiList(location: Location): Single<List<Poi>> =
        Fuel.get(buildPoiListUrl(location))
            .rxObject(PoiListResponse.Deserializer())
            .flatMap { result ->
                val (response, error) = result
                when (error) {
                    null -> Single.just(response?.poiList)
                    else -> Single.error(
                        UnableToFetchPoiDataException("Unable to retrieve POI!")
                    )
                }
            }

    fun fetchPoiDetails(pageId: Long) =
        Fuel.get(buildPoiDetailsUrl(pageId))
            .rxObject(PoiDetailsResponse.Deserializer())
            .flatMap { result ->
                val (response, error) = result
                when (error) {
                    null -> Single.just(response?.details)
                    else -> Single.error(
                        UnableToFetchPoiDataException("Unable to retrieve POI data...")
                    )
                }
            }

    fun fetchImagesUrl(imageName: List<String>): Single<List<String>> =
        Fuel.get(buildImageUrl(imageName.toTypedArray()))
            .rxObject(ImagesUrlsResponse.Deserializer())
            .flatMap { result ->
                val (response, error) = result
                when (error) {
                    null -> Single.just(
                        response?.urls?.filterNot { it.endsWith(".svg") } ?: listOf()
                    )
                    else -> Single.error(
                        UnableToFetchPoiDataException("Unable to retrieve POI data...")
                    )
                }
            }


    private fun buildPoiListUrl(location: Location) =
        POI_LIST_BASE_URL +
            "?action=query" +
            "&list=geosearch" +
            "&gsradius=$RADIUS" +
            "&gscoord=${location.latitude}|${location.longitude}" +
            "&gslimit=$POI_LIMIT" +
            "&format=json"

    private fun buildPoiDetailsUrl(pageId: Long) =
        POI_LIST_BASE_URL +
            "?action=query&prop=info|description|images&pageids=$pageId&inprop=url&format=json"

    private fun buildImageUrl(imageNames: Array<String>) =
        POI_LIST_BASE_URL +
            "?action=query&titles=${imageNames.fold("") { acc, str -> "$acc|$str" }}" +
            "&prop=imageinfo&iiprop=url&format=json"


    companion object {
        private const val POI_LIST_BASE_URL = "https://en.wikipedia.org/w/api.php"
        private const val POI_LIMIT = 50
        private const val RADIUS = 10000
    }

}

data class Image(val title: String)

data class Poi(
    @SerializedName("pageid")
    val pageId: Long,
    @SerializedName("fullurl")
    val wikipediaUrl: String,
    val title: String = "",
    val lat: Double,
    val lon: Double,
    val images: List<Image>?,
    var imageUrls: List<String>?,
    val description: String? = ""
) {

    val latLng: LatLng
        get() = LatLng(lat, lon)
}

data class PoiListResponse(val query: QueryList?) {

    data class QueryList(@SerializedName("geosearch") val poiList: List<Poi>?)

    class Deserializer : ResponseDeserializable<PoiListResponse> {
        override fun deserialize(content: String): PoiListResponse? =
            Gson().fromJson(content, PoiListResponse::class.java)
    }

    val poiList: List<Poi>
        get() = query?.poiList ?: listOf()
}

data class PoiDetailsResponse(val query: QueryDetails?) {

    data class QueryDetails(val pages: HashMap<String, Poi>)

    class Deserializer : ResponseDeserializable<PoiDetailsResponse> {
        override fun deserialize(content: String): PoiDetailsResponse? =
            Gson().fromJson(content, PoiDetailsResponse::class.java)
    }

    val details: Poi?
        get() = query?.pages?.values?.firstOrNull()
}

data class ImagesUrlsResponse(val query: QueryImagesUrl?) {

    data class ImageInfo(val url: String)
    data class PoiImage(@SerializedName("imageinfo") val imageInfo: List<ImageInfo>?)
    data class QueryImagesUrl(val pages: HashMap<String, PoiImage>)

    class Deserializer : ResponseDeserializable<ImagesUrlsResponse> {
        override fun deserialize(content: String): ImagesUrlsResponse? =
            Gson().fromJson(content, ImagesUrlsResponse::class.java)
    }

    val urls: List<String>
        get() = query?.pages?.values?.mapNotNull { it.imageInfo?.firstOrNull()?.url } ?: listOf()
}

class UnableToFetchPoiDataException(message: String) : Exception(message)
