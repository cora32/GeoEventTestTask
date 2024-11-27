package io.iskopasi.geoeventtesttask.api
import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.iskopasi.geoeventtesttask.pojo.GeoEventsData
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit


val gson = Gson()

fun getRetrofit(@ApplicationContext context: Context): Retrofit = Retrofit.Builder()
    .client(getClient(context.cacheDir))
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl("https://github.com/").build()

fun getClient(cacheDir: File): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.NONE)
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(1, TimeUnit.SECONDS)
        .callTimeout(1, TimeUnit.SECONDS)
        .cache(
            Cache(
                directory = File(cacheDir, "http_cache"),
                maxSize = 10L * 1024L * 1024L // 10 MiB
            )
        )
        .build()
}

interface Rest {
    @GET("/cora32")
    suspend fun getLocations(): Response<GeoEventsData>
}