package io.iskopasi.geoeventtesttask.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.iskopasi.geoeventtesttask.BuildConfig
import io.iskopasi.geoeventtesttask.pojo.GOResult
import io.iskopasi.geoeventtesttask.pojo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

// Date utils
val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

fun Date.toStartOfDay() = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.getTime()

fun Date.toEndOfDay() = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 0)
}.getTime()

// Logging utils
val String.e: Unit
    get() {
        if (BuildConfig.DEBUG) Log.e("--> ERR:", this)
    }

// Delayed start
val delayedJobs = mutableMapOf<String, Job>()

inline fun CoroutineScope.scheduleNewTask(delay: Long, crossinline block: () -> Unit) =
    launch(Dispatchers.IO) {
        delay(delay)
        block()
    }

inline fun CoroutineScope.invokeDelayed(
    key: String,
    delay: Long,
    crossinline block: () -> Unit
) {
    // Cancel old task
    if (delayedJobs.containsKey(key)) {
        delayedJobs[key]!!.cancel()
        delayedJobs.remove(key)
    }

    // Reschedule new task
    delayedJobs[key] = scheduleNewTask(delay, block)
}

// Anti click-spam helper
var lastClickTime = 0L
inline fun clickDelay(
    delay: Long,
    block: () -> Unit
) {
    val diff = System.currentTimeMillis() - lastClickTime
    if (diff > delay) {
        lastClickTime = System.currentTimeMillis()
        block()
    }
}

// Response wrappers
fun <T> T.asOk(): GOResult<T> = GOResult(this, Status.OK)

fun <T> String.asError() = GOResult.error<T>(error = this)


// Async utils
fun ViewModel.ui(block: suspend (CoroutineScope) -> Unit): Job = viewModelScope.launch(
    Dispatchers.Main
) {
    block(this)
}

fun ViewModel.bg(block: suspend (CoroutineScope) -> Unit): Job = viewModelScope.launch(
    Dispatchers.IO
) {
    block(this)
}

fun bg(block: suspend (CoroutineScope) -> Unit): Job = CoroutineScope(Dispatchers.IO).launch {
    block(this)
}

fun ui(block: suspend (CoroutineScope) -> Unit): Job = CoroutineScope(Dispatchers.Main).launch {
    block(this)
}


fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + " "
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}