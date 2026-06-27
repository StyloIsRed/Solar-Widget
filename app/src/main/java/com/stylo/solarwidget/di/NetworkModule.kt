package com.stylo.solarwidget.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.stylo.solarwidget.data.local.SolarExposureDatabase
import com.stylo.solarwidget.data.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Singleton
    @Provides
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
    
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SolarExposureDatabase {
        return Room.databaseBuilder(
            context,
            SolarExposureDatabase::class.java,
            "solar_exposure_db"
        ).build()
    }
    
    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("solar_prefs", Context.MODE_PRIVATE)
    }
}
