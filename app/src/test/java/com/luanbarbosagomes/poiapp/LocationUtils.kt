package com.luanbarbosagomes.poiapp

import com.luanbarbosagomes.poiapp.provider.location.Location
import com.luanbarbosagomes.poiapp.provider.poi.Image
import com.luanbarbosagomes.poiapp.provider.poi.Poi

object LocationUtils {

    val helsinki = Location(lat = 10.0, long = 10.0)
    val espoo = Location(lat = 20.0, long = 20.0)
}

object PoiUtils {

    const val url = "www.img.com"

    val poi1 = Poi(1, "", "Poi 1", 0.0, 0.0, null, null)
    val poi2 = Poi(2, "", "Poi 2", 0.0, 0.0, null, null)
}