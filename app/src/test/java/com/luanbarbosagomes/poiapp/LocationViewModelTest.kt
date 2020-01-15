package com.luanbarbosagomes.poiapp

import com.luanbarbosagomes.poiapp.LocationUtils.espoo
import com.luanbarbosagomes.poiapp.LocationUtils.helsinki
import com.luanbarbosagomes.poiapp.provider.location.LocationProvider
import com.luanbarbosagomes.poiapp.provider.location.LocationViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LocationViewModelTest {

    private val locationProvider: LocationProvider = mockk()

    private val sut = LocationViewModel(locationProvider)

    @Before
    fun before() {
    }

    @Test
    fun `New location is delivered to observer`() {
        every { locationProvider.locationObservable(any()) } returns Flowable.fromArray(helsinki)

        assertNull(sut.lastLocation)

        with(sut.locationObservable().test()) {
            assertValue(helsinki)
        }

        assertEquals(helsinki, sut.lastLocation)
    }

    @Test
    fun `Current location is emitted exactly once`() {
        every {
            locationProvider.locationObservable(any())
        } returns Flowable.fromArray(helsinki, helsinki, espoo, helsinki)

        with(sut.locationObservable().test()) {
            assertEquals(3, valueCount())
            assertValueAt(0, helsinki)
            assertValueAt(1, espoo)
            assertValueAt(2, helsinki)
        }
    }
}