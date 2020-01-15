package com.luanbarbosagomes.poiapp.dagger

import com.luanbarbosagomes.poiapp.feature.main.ActivityMain
import com.luanbarbosagomes.poiapp.feature.navigation.ActivityNavigation
import com.luanbarbosagomes.poiapp.feature.main.details.PoiDetailsDialog
import com.luanbarbosagomes.poiapp.provider.navigation.DirectionsProvider
import com.luanbarbosagomes.poiapp.provider.location.LocationProvider
import com.luanbarbosagomes.poiapp.provider.location.LocationViewModel
import com.luanbarbosagomes.poiapp.feature.navigation.NavigationViewModel
import com.luanbarbosagomes.poiapp.provider.poi.PoiProvider
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface MainComponent {

    fun getPoiFetcher(): PoiProvider
    fun getLocationProvider(): LocationProvider
    fun getDirectionsProvider(): DirectionsProvider

    fun inject(activityMain: ActivityMain)
    fun inject(activityNavigation: ActivityNavigation)
    fun inject(poiDetailsDialog: PoiDetailsDialog)
}