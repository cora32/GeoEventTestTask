package io.iskopasi.geoeventtesttask.api

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import android.net.http.NetworkException
import com.google.android.gms.common.util.UidVerifier
import com.google.android.gms.location.FusedLocationProviderClient
import io.iskopasi.geoeventtesttask.KEY_DATE_END
import io.iskopasi.geoeventtesttask.KEY_DATE_START
import io.iskopasi.geoeventtesttask.KEY_DISTANCE
import io.iskopasi.geoeventtesttask.KEY_TYPE
import io.iskopasi.geoeventtesttask.pojo.GOResult
import io.iskopasi.geoeventtesttask.room.EventType
import io.iskopasi.geoeventtesttask.room.GeoEventsDao
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.utils.asError
import io.iskopasi.geoeventtesttask.utils.asOk
import io.iskopasi.geoeventtesttask.utils.dateFormatter
import io.iskopasi.geoeventtesttask.utils.e
import io.iskopasi.geoeventtesttask.utils.getRandomString
import retrofit2.HttpException
import java.net.UnknownHostException
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random


class Repository @Inject constructor(
    private val rest: Rest,
    private val dao: GeoEventsDao,
    private val locationProvider: FusedLocationProviderClient,
    private val sp: SharedPreferences,
) {
    var cachedList = emptyList<GeoEventsEntity>()

    suspend fun requestGeoEvents(): GOResult<List<GeoEventsEntity>> {
        val geoEventsResult = simulateRetrofitRequest()

        if (geoEventsResult.isError) return geoEventsResult

        cachedList = cacheGeoEvents(geoEventsResult.data!!)

        return cachedList.asOk()
    }

    suspend fun simulateRetrofitRequest(): GOResult<List<GeoEventsEntity>> {
        try {
            // Pretending im receiving locations from network
            try {
                rest.getLocations()
            }
            catch (ex: UnknownHostException) {
                // Simulating network error
                throw ex
            }
            catch (ex: Exception) {
                // Intentionally ignoring expected exceptions because this request is only needed
                // to simulate Retrofit-base network request.
                // The actual data will be generated randomly based on current location.
            }

            return generateGeoEventsBasedOnCurrentLocation().asOk()
        } catch (e: HttpException) {
            e.printStackTrace()
            return "Network exception: ${e.message}".asError()
        } catch (e: Throwable) {
            e.printStackTrace()
            return "General exception: ${e.message}".asError()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Pair<Double, Double> =
        suspendCoroutine { continuation ->
            locationProvider.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        // If location is not null, invoke the success callback with latitude and longitude
                        continuation.resume(Pair(it.latitude, it.longitude))
                    }
                }
                .addOnFailureListener { exception ->
                    // If an error occurs, invoke the failure callback with the exception
                    continuation.resumeWithException(exception)
                }
        }

    // Generates geo events around current user location
    private suspend fun generateGeoEventsBasedOnCurrentLocation(): List<GeoEventsEntity> {
        val currentLocation = getCurrentLocation()

        val result = mutableListOf<GeoEventsEntity>()
        val startLatitude = currentLocation.first - 0.5
        val startLongitude = currentLocation.second - 0.5

        var latitudeOffset = 0.0
        var longitudeOffset = 0.0

        // Generating events around current location
        for (lat in 0 until 10) {
            for (long in 0 until 10) {
                val newLat = startLatitude + latitudeOffset
                val newLong = startLongitude + longitudeOffset

                var distance = FloatArray(3)
                Location.distanceBetween(
                    currentLocation.first, currentLocation.second,
                    newLat, newLong,
                    distance
                )

                result.add(
                    GeoEventsEntity(
                        lat = newLat,
                        long = newLong,
                        name = "${getRandomString(5)} ${getRandomString(8)}",
                        description = getRandomString(150),
                        date = Date((0..System.currentTimeMillis()).random()),
                        type = EventType.entries.toTypedArray().random(),
                        distance = distance[0].toInt()
                    )
                )

                longitudeOffset += 0.01
            }
            latitudeOffset += 0.01
        }

        return result
    }

    private fun cacheGeoEvents(geoEvents: List<GeoEventsEntity>): List<GeoEventsEntity> {
        dao.insert(geoEvents)

        return dao.getAll()
    }

    fun getTypeFilter(): String = sp.getString(KEY_TYPE, EventType.None.name) ?: EventType.None.name

    fun getDateFilterStart(): Long = sp.getLong(KEY_DATE_START, 0L)

    fun getDateFilterEnd(): Long = sp.getLong(KEY_DATE_END, System.currentTimeMillis())

    fun getDistanceFilter(): Int = sp.getInt(KEY_DISTANCE, 100000)

    fun saveTypeFilter(type: String) = sp.edit().apply {
        putString(KEY_TYPE, type)
        apply()
    }

    fun saveDateFilterStart(dateStart: Long) = sp.edit().apply {
        putLong(KEY_DATE_START, dateStart)
        apply()
    }

    fun saveDateFilterEnd(dateEnd: Long) = sp.edit().apply {
        putLong(KEY_DATE_END, dateEnd)
        apply()
    }

    fun saveDistanceFilter(distance: Int) = sp.edit().apply {
        putInt(KEY_DISTANCE, distance)
        apply()
    }

    suspend fun getDataByUid(uid: Int) = dao.getByUid(uid)
}