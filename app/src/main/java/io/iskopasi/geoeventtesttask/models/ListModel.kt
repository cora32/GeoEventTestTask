package io.iskopasi.geoeventtesttask.models

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.geoeventtesttask.api.Repository
import io.iskopasi.geoeventtesttask.pojo.GeoEventsData
import io.iskopasi.geoeventtesttask.pojo.Status
import io.iskopasi.geoeventtesttask.room.EventType
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.utils.bg
import io.iskopasi.geoeventtesttask.utils.dateFormatter
import io.iskopasi.geoeventtesttask.utils.e
import io.iskopasi.geoeventtesttask.utils.ui
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.ranges.contains
import kotlin.ranges.rangeTo

@HiltViewModel
class ListUIModel @Inject constructor(
    context: Application,
    private val repository: Repository,
) : AndroidViewModel(context) {
    val errorFlow = MutableStateFlow<String?>(null)
    val isLoading = mutableStateOf<Boolean>(false)
    val geoData = mutableStateOf<List<GeoEventsEntity>>(emptyList<GeoEventsEntity>())
    val currentTypeFilter = mutableStateOf<EventType>(EventType.None)
    val currentDateFilterStart = mutableLongStateOf(0L)
    val currentDateFilterEnd = mutableLongStateOf(0L)
    val currentDistanceFilter = mutableIntStateOf(0)

    init {
        //Load filter values
        currentTypeFilter.value = when (repository.getTypeFilter()) {
            EventType.Concert.name -> EventType.Concert
            EventType.Workshop.name -> EventType.Workshop
            EventType.Etc.name -> EventType.Etc
            else -> EventType.None
        }
        currentDateFilterStart.longValue = repository.getDateFilterStart()
        currentDateFilterEnd.longValue = repository.getDateFilterEnd()
        currentDistanceFilter.intValue = repository.getDistanceFilter()
    }

    fun requestGeoData() = bg {
        isLoading.value = true

        // Request events
        val result = repository.requestGeoEvents()

        // Parse response
        when (result.status) {
            Status.OK -> {
                geoData.value = result.data!!.filtered()
            }

            Status.Error -> {
                errorFlow.emit("Error -> ${result.error}")
            }

            Status.Unknown -> {}
        }

        isLoading.value = false
    }

    fun applyFilterByType(type: EventType) {
        currentTypeFilter.value = type
        repository.saveTypeFilter(type.name)

        applyFilters()
    }

    fun applyFilterByDateStart(dateStart: Long) {
        currentDateFilterStart.longValue = dateStart
        repository.saveDateFilterStart(dateStart)

        applyFilters()
    }

    fun applyFilterByDateEnd(dateEnd: Long) {
        currentDateFilterEnd.longValue = dateEnd
        repository.saveDateFilterEnd(dateEnd)

        applyFilters()
    }

    fun applyFilterByDistance(distance: Int) {
        currentDistanceFilter.intValue = distance
        repository.saveDistanceFilter(distance)

        applyFilters()
    }

    private fun applyFilters() = bg {
        isLoading.value = true

        geoData.value = repository.cachedList.filtered()

        isLoading.value = false
    }

    private fun List<GeoEventsEntity>.filtered(): List<GeoEventsEntity> = filter {
        it.distance < currentDistanceFilter.intValue
    }.filter {
        it.date?.time in currentDateFilterStart.longValue until currentDateFilterEnd.longValue
    }.filter {
        if (currentTypeFilter.value == EventType.None) true else it.type == currentTypeFilter.value
    }

    fun testFilter(list: List<GeoEventsEntity>): List<GeoEventsEntity> {
        return list.filtered()
    }
}

