package io.iskopasi.geoeventtesttask.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class EventType {
    Concert,
    Workshop,
    Etc,
    None,
}

@Entity
data class GeoEventsEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(index = true, name = "lat") val lat: Double = 0.0,
    @ColumnInfo(index = true, name = "long") val long: Double = 0.0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "type") val type: EventType = EventType.None,
    @ColumnInfo(name = "date") val date: Date?= null,
    @ColumnInfo(name = "distance") val distance: Int = 0,
)