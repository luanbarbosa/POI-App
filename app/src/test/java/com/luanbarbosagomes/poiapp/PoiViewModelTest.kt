package com.luanbarbosagomes.poiapp

import com.luanbarbosagomes.poiapp.LocationUtils.espoo
import com.luanbarbosagomes.poiapp.LocationUtils.helsinki
import com.luanbarbosagomes.poiapp.PoiUtils.poi1
import com.luanbarbosagomes.poiapp.PoiUtils.poi2
import com.luanbarbosagomes.poiapp.provider.poi.PoiProvider
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Test

class PoiViewModelTest {

    private val poiProvider: PoiProvider = mockk()
    private val sut = PoiViewModel(poiProvider)

    @Test
    fun `List of POIs will be emitted when new location is provided`() {
        every { poiProvider.fetchPoiList(any()) } returns Single.just(listOf(poi1))

        with(sut.poiListSubject.test()) {
            sut.fetchPoiData(helsinki)
            assertEquals(1, valueCount())
            assertEquals(values().first().first(), poi1)
        }
    }

    @Test
    fun `Emitted list of POIs are based on the location provided`() {
        every { poiProvider.fetchPoiList(helsinki) } returns Single.just(listOf(poi1))
        every { poiProvider.fetchPoiList(espoo) } returns Single.just(listOf(poi2))

        with(sut.poiListSubject.test()) {
            sut.fetchPoiData(helsinki)
            sut.fetchPoiData(espoo)

            assertEquals(2, valueCount())
            assertValueAt(0, listOf(poi1))
            assertValueAt(1, listOf(poi2))
        }
    }
}