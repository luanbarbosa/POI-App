package com.luanbarbosagomes.poiapp.dagger

import com.luanbarbosagomes.poiapp.feature.main.MainActivity
import com.luanbarbosagomes.poiapp.provider.location.LocationProvider
import com.luanbarbosagomes.poiapp.provider.location.LocationViewModel
import com.luanbarbosagomes.poiapp.provider.poi.PoiProvider
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface MainComponent {

    fun getPoiFetcher(): PoiProvider
    fun getLocationProvider(): LocationProvider

    fun inject(poiViewModel: PoiViewModel)
    fun inject(mainActivity: MainActivity)
    fun inject(locationViewModel: LocationViewModel)

}