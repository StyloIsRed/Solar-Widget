package com.stylo.solarwidget.data.repository

import android.content.SharedPreferences
import com.stylo.solarwidget.data.local.SolarExposureDatabase
import com.stylo.solarwidget.data.local.SolarExposureEntity
import com.stylo.solarwidget.data.remote.WeatherApi
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolarExposureRepository @Inject constructor(
    private val database: SolarExposureDatabase,
    private val weatherApi: WeatherApi,
    private val prefs: SharedPreferences
) {
    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val apiKey by lazy { prefs.getString("weather_api_key", "") ?: "" }
    
    suspend fun updateSolarExposure(latitude: Double, longitude: Double) {
        try {
            Timber.d("Fetching weather data for lat=$latitude, lon=$longitude")
            val response = weatherApi.getCurrentWeather(latitude, longitude, apiKey)
            val cloudCoverage = response.clouds.all
            val solarExposure = 100 - cloudCoverage
            
            val today = LocalDate.now().format(dateFormatter)
            val entity = SolarExposureEntity(
                date = today,
                cloudCoverage = cloudCoverage,
                solarExposure = solarExposure
            )
            
            database.solarExposureDao().insert(entity)
            
            // Cache in SharedPreferences for widget access
            prefs.edit().apply {
                putInt("solar_exposure", solarExposure)
                putInt("cloud_coverage", cloudCoverage)
                putLong("last_update", System.currentTimeMillis())
                apply()
            }
            
            Timber.d("Solar exposure updated: $solarExposure%, Cloud coverage: $cloudCoverage%")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update solar exposure")
        }
    }
    
    suspend fun shouldNotifyUser(): Boolean {
        try {
            val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)
            val yesterdayData = database.solarExposureDao().getByDate(yesterday)
            
            // Notify if yesterday's cloud coverage was > 50%
            val shouldNotify = yesterdayData != null && yesterdayData.cloudCoverage > 50
            Timber.d("Should notify: $shouldNotify (yesterday cloud: ${yesterdayData?.cloudCoverage}%)")
            return shouldNotify
        } catch (e: Exception) {
            Timber.e(e, "Error checking notification condition")
            return false
        }
    }
    
    fun observeTodayData(): Flow<SolarExposureEntity?> {
        val today = LocalDate.now().format(dateFormatter)
        return database.solarExposureDao().observeByDate(today)
    }
    
    fun observeAllData(): Flow<List<SolarExposureEntity>> {
        return database.solarExposureDao().observeAll()
    }
    
    suspend fun getLatest(): SolarExposureEntity? {
        return database.solarExposureDao().getLatest()
    }
}
