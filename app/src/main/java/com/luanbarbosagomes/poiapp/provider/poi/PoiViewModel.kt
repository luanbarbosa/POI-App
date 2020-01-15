package com.luanbarbosagomes.poiapp.provider.poi

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * [ViewModel] responsible for operations related to Points of Interest (POI), such as POI list
 * request based on location and POI details.
 */
class PoiViewModel @Inject constructor(
    var poiProvider: PoiProvider
) : ViewModel() {

    private val disposeBag = CompositeDisposable()
    private val poiListSubject: PublishSubject<List<Poi>> = PublishSubject.create()
    private val errorSubject: PublishSubject<Throwable> = PublishSubject.create()

    fun poiObservable(): Observable<List<Poi>> = poiListSubject
    fun errorObservable(): Observable<Throwable> = errorSubject

    fun fetchPoiData(location: com.luanbarbosagomes.poiapp.provider.location.Location) {
        poiProvider
            .fetchPoiList(location)
            .subscribe { poiList, error ->
                poiList?.let { poiListSubject.onNext(it) }
                error?.let { errorSubject.onNext(error) }
            }
            .addTo(disposeBag)
    }

    fun poiDetailsObservable(poi: Poi): Single<Poi?> =
        poiProvider
            .fetchPoiDetails(poi.pageId)
            .flatMap { poiDetails ->
                poiProvider
                    .fetchImagesUrl(poiDetails.images?.map { it.title } ?: listOf())
                    .onErrorReturn { null }
                    .map { urls -> poiDetails.apply { imageUrls = urls } }
            }
            .subscribeOn(Schedulers.io())
}