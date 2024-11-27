package io.iskopasi.geoeventtesttask.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.iskopasi.geoeventtesttask.converters.Converters

@Database(
    entities = [GeoEventsEntity::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class GeoEventsDB : RoomDatabase() {
    abstract fun dao(): GeoEventsDao
}

fun getDB(
    application: Context
): GeoEventsDB = Room
    .databaseBuilder(application, GeoEventsDB::class.java, "geoevents_db")
    .fallbackToDestructiveMigration()
    .build()
