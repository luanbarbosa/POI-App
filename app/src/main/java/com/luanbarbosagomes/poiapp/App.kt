package com.luanbarbosagomes.poiapp

import android.app.Application
import android.content.Context
import com.luanbarbosagomes.poiapp.dagger.DaggerMainComponent
import com.luanbarbosagomes.poiapp.dagger.MainComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        daggerMainComponent = DaggerMainComponent.create()
    }

    companion object {
        lateinit var context: Context
            private set

        lateinit var daggerMainComponent: MainComponent
            private set
    }
}