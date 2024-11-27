package io.iskopasi.geoeventtesttask.modules

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.iskopasi.geoeventtesttask.api.Repository
import io.iskopasi.geoeventtesttask.api.Rest
import io.iskopasi.geoeventtesttask.api.getRetrofit
import io.iskopasi.geoeventtesttask.room.GeoEventsDao
import io.iskopasi.geoeventtesttask.room.getDB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModules {
    @Provides
    @Singleton
    fun getRepo(@ApplicationContext context: Context): Repository = Repository(
        getRest(context),
        getDao(context),
        getLocationService(context),
        getSharedPreferences(context)
    )

    @Provides
    @Singleton
    fun getRest(@ApplicationContext context: Context): Rest =
        getRetrofit(context).create(Rest::class.java)

    @Provides
    @Singleton
    fun getDao(@ApplicationContext context: Context): GeoEventsDao =
        getDB(context).dao()

    @Provides
    @Singleton
    fun getLocationService(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun getSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("GeoEventSP", MODE_PRIVATE)
}