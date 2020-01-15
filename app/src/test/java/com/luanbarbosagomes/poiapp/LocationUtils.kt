package com.luanbarbosagomes.poiapp

import com.luanbarbosagomes.poiapp.provider.location.Location
import com.luanbarbosagomes.poiapp.provider.poi.Image
import com.luanbarbosagomes.poiapp.provider.poi.Poi

object LocationUtils {

    val helsinki = Location(lat = 10.0, long = 10.0)

    val espoo = Location(lat = 20.0, long = 20.0)

    val vantaa = Location(lat = 30.0, long = 30.0)
}

object PoiUtils {

    val poi1 = Poi(1, "", "Poi 1", 0.0, 0.0, null, null)
    val poi1WithDetails = Poi(
        1,
        "wikiPoi1",
        "Poi 1",
        0.0,
        0.0,
        listOf(Image("img1"), Image("img2")),
        null,
        "Poi 1 desc")
    val poi1Complete = Poi(
        1,
        "wikiPoi1",
        "Poi 1",
        0.0,
        0.0,
        listOf(Image("img1"), Image("img2")),
        listOf("www.img1", "www.img2"),
        "Poi 1 desc")

    val poi2 = Poi(2, "", "Poi 2", 0.0, 0.0, null, null)
    val poi2WithDetails = Poi(
        1,
        "wikiPoi2",
        "Poi 2",
        0.0,
        0.0,
        listOf(Image("img1"), Image("img2")),
        null,
        "Poi 2 desc")
    val poi2Complete = Poi(
        1,
        "wikiPoi2",
        "Poi 2",
        0.0,
        0.0,
        listOf(Image("img1"), Image("img2")),
        listOf("www.img1", "www.img2"),
        "Poi 2 desc")
}