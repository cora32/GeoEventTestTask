package io.iskopasi.geoeventtesttask.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Colors
import android.provider.CalendarContract.Events
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.iskopasi.geoeventtesttask.GEO_EVENT_UID
import io.iskopasi.geoeventtesttask.models.ListUIModel
import io.iskopasi.geoeventtesttask.models.ResultUIModel
import io.iskopasi.geoeventtesttask.room.GeoEventsEntity
import io.iskopasi.geoeventtesttask.ui.theme.GeoEventTestTaskTheme
import kotlin.getValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.iskopasi.geoeventtesttask.R
import io.iskopasi.geoeventtesttask.ui.theme.Purple80
import io.iskopasi.geoeventtesttask.ui.theme.purpleColor4
import io.iskopasi.geoeventtesttask.utils.dateFormatter
import io.iskopasi.geoeventtesttask.utils.dateTimeFormatter
import io.iskopasi.geoeventtesttask.utils.toEndOfDay
import io.iskopasi.geoeventtesttask.utils.toStartOfDay

@AndroidEntryPoint
class ResultActivity : ComponentActivity() {
    private val model: ResultUIModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get data uid and request details by the uid
        val uid = intent.getIntExtra(GEO_EVENT_UID, -1)
        model.requestDataByUid(uid)

        // Lock orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        enableEdgeToEdge()

        setContent {
            GeoEventTestTaskTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    floatingActionButton = {
                        SmallFloatingActionButton(
                            onClick = {
                                addToCalendar(model.dataState.value)
                            },
                            containerColor = purpleColor4,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    stringResource(R.string.add_to_calendar),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.W400
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    Icons.Filled.AddCircle,
                                    "Add to Calendar",
                                    modifier = Modifier.padding(start = 8.dp),
                                    tint = Color.White
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
                        InfoContent(model)
                    }
                }
            }
        }
    }

    private fun addToCalendar(data: GeoEventsEntity) {
        startActivity(Intent(Intent.ACTION_INSERT).apply {
            setData(Events.CONTENT_URI)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, data.date!!.toStartOfDay())
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, data.date.toEndOfDay())
                    putExtra(Events.TITLE, data.name)
                    putExtra(Events.DESCRIPTION, data.description)
                    putExtra(Events.EVENT_LOCATION, "${data.lat} ${data.long}")
                    putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
        })
    }
}

// -------------

@Composable
private fun InfoContent(model: ResultUIModel) {
    val data by model.dataState

    Box(modifier = Modifier
        .padding(32.dp)
        .fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .padding(vertical = 64.dp)
                .align(Alignment.CenterHorizontally)) {
                Text(
                    stringResource(R.string.details),
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            DetailItem(stringResource(R.string.name), data.name)
            HorizontalDivider()
            DetailItem(stringResource(R.string.description), data.description)
            HorizontalDivider()
            DetailItem(stringResource(R.string.time), dateTimeFormatter.format(data.date))
            HorizontalDivider()
            DetailItem(stringResource(R.string.location), "lat: ${data.lat} long: ${data.long}")
            HorizontalDivider()
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Box(
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                value,

                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}