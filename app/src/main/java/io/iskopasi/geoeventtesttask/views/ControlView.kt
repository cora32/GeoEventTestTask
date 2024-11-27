package io.iskopasi.geoeventtesttask.views

import android.widget.EditText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.iskopasi.geoeventtesttask.R
import io.iskopasi.geoeventtesttask.models.ListUIModel
import io.iskopasi.geoeventtesttask.room.EventType
import io.iskopasi.geoeventtesttask.utils.dateFormatter
import io.iskopasi.geoeventtesttask.utils.e
import java.util.Date
import java.util.Locale


@Composable
fun TypeFilterButton(text: String, width: Dp, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.width(width),
        onClick = onClick,
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlView(model: ListUIModel) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = model.currentDateFilterStart.longValue,
        initialSelectedEndDateMillis = model.currentDateFilterEnd.longValue,
        yearRange = 1900..2024,
        initialDisplayMode = DisplayMode.Picker,
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(model.currentDistanceFilter.intValue.toString()) }
    val pattern = remember { Regex("^\\d+\$") }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                model.applyFilterByDateStart(state.selectedStartDateMillis ?: 0)
                model.applyFilterByDateEnd(state.selectedEndDateMillis ?: 0)

                TextButton(
                    onClick = { showDatePicker = false },
                )
                { Text("Ok") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false })
                { Text("Cancel") }
            }
        ) {
            Box(modifier = Modifier.height(400.dp)) {
                DateRangePicker(
                    state = state
//                    ),
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(300.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${stringResource(R.string.filter_by_type)}: ${model.currentTypeFilter.value}")
            Row() {
                for (event in EventType.entries)
                    TypeFilterButton(event.name, width = 90.dp) { model.applyFilterByType(event) }
            }
            HorizontalDivider()
            Text(
                stringResource(R.string.filter_by_date),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            TypeFilterButton(
                "${dateFormatter.format(state.selectedStartDateMillis ?: 0)} " +
                        "- ${dateFormatter.format(state.selectedEndDateMillis ?: 0)}",
                width = 200.dp
            ) {
                showDatePicker = true
            }
            HorizontalDivider()
            Text(
                stringResource(R.string.filter_by_distance),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TextField(
                singleLine = true,
                maxLines = 1,
                value = text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                onValueChange = {
                    text = it
                    if (it.isNotEmpty() && it.matches(pattern)) {
                        model.applyFilterByDistance(it.toInt())
                    }
                },
                label = { Text("Meters") }
            )
        }
    }
}