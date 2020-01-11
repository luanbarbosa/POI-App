package com.luanbarbosagomes.poiapp.dagger

import com.luanbarbosagomes.poiapp.MainActivity
import com.luanbarbosagomes.poiapp.PoiFetcher
import com.luanbarbosagomes.poiapp.PoiViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface MainComponent {

    fun getPoiFetcher(): PoiFetcher

    fun inject(poiViewModel: PoiViewModel)
    fun inject(mainActivity: MainActivity)

}