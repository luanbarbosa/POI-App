package com.luanbarbosagomes.poiapp.provider.poi

import android.location.Location
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.rx.rxObject
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
                    else -> Single.error(UnableToFetchPoiDataException())
                }
            }

    fun fetchPoiDetails(pageId: Long) =
        Fuel.get(buildPoiDetailsUrl(pageId))
            .rxObject(PoiDetailsResponse.Deserializer())
            .flatMap { result ->
                val (response, error) = result
                when (error) {
                    null -> Single.just(response?.details)
                    else -> Single.error(UnableToFetchPoiDataException())
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
                    else -> Single.error(UnableToFetchPoiDataException())
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

class UnableToFetchPoiDataException(
    message: String = "Unable to retrieve POI data..."
) : Exception(message)
