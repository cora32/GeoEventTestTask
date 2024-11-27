package io.iskopasi.geoeventtesttask.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface GeoEventsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(geoEvents: List<GeoEventsEntity>)

    @Query("SELECT * FROM geoeventsentity")
    fun getAll(): List<GeoEventsEntity>

    @Query("SELECT * FROM geoeventsentity WHERE uid = :uid")
    fun getByUid(uid: Int): GeoEventsEntity

    @Query("DELETE FROM geoeventsentity")
    fun clear()
}