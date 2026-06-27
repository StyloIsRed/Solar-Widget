package com.stylo.solarwidget.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    @SerializedName("clouds")
    val clouds: CloudData,
    @SerializedName("main")
    val main: MainData,
    @SerializedName("name")
    val locationName: String
)

data class CloudData(
    @SerializedName("all")
    val all: Int // Cloud coverage 0-100%
)

data class MainData(
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double
)

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
