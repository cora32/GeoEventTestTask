package io.iskopasi.geoeventtesttask.models

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.geoeventtesttask.api.Repository
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.utils.bg
import javax.inject.Inject

@HiltViewModel
class ResultUIModel @Inject constructor(
    context: Application,
    private val repository: Repository,
) : AndroidViewModel(context) {
    val dataState = mutableStateOf<GeoEventsEntity>(GeoEventsEntity())

    fun requestDataByUid(uid: Int) = bg  {
        dataState.value = repository.getDataByUid(uid)
    }
}
