package io.iskopasi.geoeventtesttask.pojo

import java.util.Date

data class GeoEventsData(val name: String,
                         val type: String,
                         val date: Date,
                         val long: Double,
                         val lat: Double)