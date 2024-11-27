package io.iskopasi.geoeventtesttask

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.location.LocationServices
import io.iskopasi.geoeventtesttask.api.Repository
import io.iskopasi.geoeventtesttask.api.Rest
import io.iskopasi.geoeventtesttask.api.getRetrofit
import io.iskopasi.geoeventtesttask.models.ListUIModel
import io.iskopasi.geoeventtesttask.room.EventType
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.room.getDB
import io.iskopasi.geoeventtesttask.utils.e

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.Date

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val app: Application = getApplicationContext()
//    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val rest = getRetrofit(app).create(Rest::class.java)
    val dao = getDB(app).dao()
    val locationService = LocationServices.getFusedLocationProviderClient(app)
    val sp = app.getSharedPreferences("GeoTestSP", MODE_PRIVATE)
    val repository = Repository(
        rest,
        dao,
        locationService,
        sp
    )

    @Test
    fun distanceFilterText() {
        val model: ListUIModel = ListUIModel(app, repository)

        // Filling with test data
        val data = mutableListOf<GeoEventsEntity>()
        for(i in 0 until 100) {
            data.add(GeoEventsEntity(
                distance = i,
                date = Date(0L)))
        }

        // Must contain 10 items
        model.applyFilterByDistance(10)
        val filteredList10 = model.testFilter(data)
        assertEquals(10, filteredList10.size)

        // Must contain 20 items
        model.applyFilterByDistance(20)
        val filteredList20 = model.testFilter(data)
        assertEquals(20, filteredList20.size)
    }

    @Test
    fun dateFilterText() {
        val model: ListUIModel = ListUIModel(app, repository)

        // Filling with test data
        val data = mutableListOf<GeoEventsEntity>()
        for(i in 0 until 100) {
            data.add(GeoEventsEntity(
                date = Date(i.toLong())))
        }

        // Must contain 10 items
        model.applyFilterByDateStart(0)
        model.applyFilterByDateEnd(10)
        val filteredList10 = model.testFilter(data)
        assertEquals(10, filteredList10.size)

        // Must contain 20 items
        model.applyFilterByDateStart(20)
        model.applyFilterByDateEnd(40)
        val filteredList20 = model.testFilter(data)
        assertEquals(20, filteredList20.size)

        // Must contain 50 items
        model.applyFilterByDateStart(0)
        model.applyFilterByDateEnd(50)
        val filteredList50 = model.testFilter(data)
        assertEquals(50, filteredList50.size)
    }
}