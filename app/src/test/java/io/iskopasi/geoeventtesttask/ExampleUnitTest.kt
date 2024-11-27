package io.iskopasi.geoeventtesttask

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.os.Build.VERSION_CODES.Q
import androidx.activity.viewModels
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.runner.AndroidJUnit4
import com.google.android.gms.location.LocationServices
import io.iskopasi.geoeventtesttask.api.Repository
import io.iskopasi.geoeventtesttask.api.Rest
import io.iskopasi.geoeventtesttask.api.getRetrofit
import io.iskopasi.geoeventtesttask.models.ListUIModel
import io.iskopasi.geoeventtesttask.modules.HiltModules_GetRepoFactory.getRepo
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.room.getDB
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import kotlin.getValue

class ExampleUnitTest {
}