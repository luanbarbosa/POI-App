package com.luanbarbosagomes.poiapp

import android.location.Location
import androidx.lifecycle.ViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

class PoiViewModel : ViewModel() {

    private val poiFetcher: PoiFetcher = PoiFetcher() // TODO - Use dagger to inject this here

    private var lastLocationRequested: Location? = null

    private val disposeBag = CompositeDisposable()
    private val poiListSubject: PublishSubject<List<Poi>> = PublishSubject.create()

    fun poiObservable(): Flowable<List<Poi>> =
        poiListSubject.toFlowable(BackpressureStrategy.LATEST)

    fun fetchPoiData(location: Location) {
        if (lastLocationRequested != location) { // avoid duplicated requests
            poiFetcher
                .fetchPoi(location)
                .subscribe { poiList ->
                    poiList?.let { poiListSubject.onNext(it) }
                }
                .addTo(disposeBag)
        }
    }
}