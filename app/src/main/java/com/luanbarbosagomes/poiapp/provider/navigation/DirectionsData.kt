package com.luanbarbosagomes.poiapp.provider.navigation

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class DirectionsResponse(val routes: List<Routes>) {

    class Deserializer : ResponseDeserializable<DirectionsResponse> {
        override fun deserialize(content: String): DirectionsResponse? =
            Gson().fromJson(content, DirectionsResponse::class.java)
    }

    data class Routes (
        val bounds : Bounds,
        val legs : List<Legs>,
        @SerializedName("overview_polyline") val overviewPolyline : Polyline
    )

    data class Bounds (val northeast : LatLong, val southwest : LatLong)

    data class Legs (
        val distance : TextValue,
        val duration : TextValue,
        val steps : List<Step>,
        @SerializedName("end_address") val endAddress : String,
        @SerializedName("end_location") val endLocation : LatLong,
        @SerializedName("start_address") val startAddress : String,
        @SerializedName("start_location") val startLocation : LatLong,
        @SerializedName("traffic_speed_entry") val trafficSpeedEntry : List<String>,
        @SerializedName("via_waypoint") val viaWaypoint : List<String>
    )

    open class Step (
        val distance : TextValue = TextValue.empty(),
        val duration : TextValue = TextValue.empty(),
        val polyline : Polyline = Polyline(),
        @SerializedName("end_location") val endLocation : LatLong = LatLong(),
        @SerializedName("html_instructions") val htmlInstructions : String = "",
        @SerializedName("start_location") val startLocation : LatLong = LatLong(),
        @SerializedName("travel_mode") val travelMode : String = ""
    )

    data class Polyline (val points : String = "")
    data class LatLong (val lat : Double = 0.0, val lng : Double = 0.0)
    data class TextValue (val text : String, val value : Int) {
        companion object {
            fun empty() = TextValue("", 0)
        }
    }
}