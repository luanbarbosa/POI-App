package com.luanbarbosagomes.poiapp.provider.poi

import android.location.Location
import androidx.lifecycle.ViewModel
import com.luanbarbosagomes.poiapp.dagger.DaggerMainComponent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PoiViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var poiProvider: PoiProvider

    private val disposeBag = CompositeDisposable()
    private val poiListSubject: PublishSubject<List<Poi>> = PublishSubject.create()
    private val errorSubject: PublishSubject<Throwable> = PublishSubject.create()

    init {
        DaggerMainComponent.create().inject(this)
    }

    fun errorObservable(): Observable<Throwable> = errorSubject

    fun poiObservable(): Flowable<List<Poi>> =
        poiListSubject.toFlowable(BackpressureStrategy.LATEST)

    fun fetchPoiData(location: Location) {
        poiProvider
            .fetchPoi(location)
            .subscribe { poiList, error ->
                poiList?.let { poiListSubject.onNext(it) }
                error?.let { errorSubject.onNext(error) }
            }
            .addTo(disposeBag)
    }
}