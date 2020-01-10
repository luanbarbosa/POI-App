package com.luanbarbosagomes.poiapp

import android.location.Location
import androidx.lifecycle.ViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

class PoiViewModel : ViewModel() {

    private val poiFetcher: PoiFetcher = PoiFetcher() // TODO - Use dagger to inject this here

    private var lastLocationRequested: Location? = null

    private val disposeBag = CompositeDisposable()
    private val poiListSubject: PublishSubject<List<Poi>> = PublishSubject.create()
    private val errorSubject: PublishSubject<Throwable> = PublishSubject.create()

    fun errorObservable(): Observable<Throwable> = errorSubject

    fun poiObservable(): Flowable<List<Poi>> =
        poiListSubject.toFlowable(BackpressureStrategy.LATEST)

    fun fetchPoiData(location: Location) {
        if (lastLocationRequested != location) { // avoid duplicated requests
            poiFetcher
                .fetchPoi(location)
                .subscribe { poiList, error ->
                    poiList?.let { poiListSubject.onNext(it) }
                    error?.let { errorSubject.onNext(error) }
                }
                .addTo(disposeBag)
        }
    }
}