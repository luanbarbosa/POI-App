package com.luanbarbosagomes.poiapp.provider.poi

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

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