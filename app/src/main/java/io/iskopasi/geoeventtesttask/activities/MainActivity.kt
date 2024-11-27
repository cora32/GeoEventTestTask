@file:OptIn(ExperimentalMaterial3Api::class)

package io.iskopasi.geoeventtesttask.activities

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import dagger.hilt.android.AndroidEntryPoint
import io.iskopasi.geoeventtesttask.GEO_EVENT_UID
import io.iskopasi.geoeventtesttask.R
import io.iskopasi.geoeventtesttask.activities.EmptyView
import io.iskopasi.geoeventtesttask.models.ListUIModel
import io.iskopasi.geoeventtesttask.room.EventType
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.ui.theme.GeoEventTestTaskTheme
import io.iskopasi.geoeventtesttask.ui.theme.PurpleGrey40
import io.iskopasi.geoeventtesttask.utils.dateFormatter
import io.iskopasi.geoeventtesttask.utils.ui
import io.iskopasi.geoeventtesttask.views.ControlView
import java.util.Date
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val permissionsGranted = permissions.values.reduce { acc, next ->
            acc && next
        }

        if (permissionsGranted) {
            model.requestGeoData()
        } else {
            showError(getString(R.string.permission_error))
        }
    }
    private val model: ListUIModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lock orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        enableEdgeToEdge()

        // Request permission or request geolocations
        if (!permissionGranted()) {
            // Request location permissions
            requestLocationPermissions()
        } else {
            model.requestGeoData()
        }

        setContent {
            val focusManager = LocalFocusManager.current
            val snackState = SnackbarHostState()
            val errorFlow by model.errorFlow.collectAsState()

            // Show snackbar with error and close keyboard on error
            if (errorFlow != null) {
                LaunchedEffect(errorFlow) {
                    focusManager.clearFocus()
                    snackState.showSnackbar(
                        errorFlow!!,
                        duration = SnackbarDuration.Long
                    )
                }
            }

            GeoEventTestTaskTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    snackbarHost = {
                        SnackbarHost(hostState = snackState) {
                            if (snackState.currentSnackbarData != null) {
                                Snackbar(
                                    actionColor = Color.Yellow,
                                    contentColor = Color.White,
                                    containerColor = Color.Red,
                                    snackbarData = snackState.currentSnackbarData!!
                                )
                            }
                        }
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Content(model)
                    }
                }
            }
        }
    }

    private fun showError(str: String) = ui {
        model.errorFlow.emit(str)
    }

    private fun permissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

// -------------

@Composable
private fun BoxScope.Content(model: ListUIModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {
            DataListView(model)
        }
        HorizontalDivider()
        Box(modifier = Modifier.height(300.dp)) {
            ControlView(model)
        }
    }
}

@Composable
private fun BoxScope.LoaderView() {
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .align(alignment = Alignment.Center),
    )
}

@Composable
private fun BoxScope.EmptyView() {
    Text(
        stringResource(R.string.no_events),
        modifier = Modifier
            .fillMaxWidth()
            .align(alignment = Alignment.Center),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun BoxScope.DataListView(model: ListUIModel) {
    val geoDataList by model.geoData
    val isLoading by model.isLoading
    val context = LocalContext.current

    when {
        isLoading -> LoaderView()
        geoDataList.isEmpty() -> EmptyView()
        else ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(geoDataList.size, key = { it }) {
                    val geoEventData = geoDataList[it]
                    UserListItem(geoEventData) {
                        context.startActivity(Intent(context, ResultActivity::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                putExtra(GEO_EVENT_UID, geoEventData.uid)
                            })
                    }
                }
            }
    }
}

@Composable
private fun UserListItem(geoEventData: GeoEventsEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Column(modifier = Modifier) {
            Text(
                "Name: ${geoEventData.name}",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                geoEventData.type.name,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.requiredWidth(150.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                if (geoEventData.date == null) stringResource(R.string.no_date)
                else dateFormatter.format(geoEventData.date),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
//                "${geoEventData.lat} ${geoEventData.long} (${geoEventData.distance}m)",
                "Distance: ${geoEventData.distance}m",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
