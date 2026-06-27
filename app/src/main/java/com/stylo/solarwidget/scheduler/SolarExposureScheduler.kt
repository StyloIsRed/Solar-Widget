package com.stylo.solarwidget.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.stylo.solarwidget.worker.SolarExposureWorker
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolarExposureScheduler @Inject constructor(private val context: Context) {
    
    fun scheduleDaily6AmCheck() {
        try {
            // Calculate delay until 6 AM
            val now = LocalDateTime.now()
            val sixAm = now.withHour(6).withMinute(0).withSecond(0)
            
            val scheduledTime = if (now.isBefore(sixAm)) {
                sixAm
            } else {
                sixAm.plusDays(1)
            }
            
            val delay = Duration.between(now, scheduledTime)
            
            Timber.d("Scheduling solar exposure check for 6 AM daily. Initial delay: ${delay.seconds} seconds")
            
            val solarWork = PeriodicWorkRequestBuilder<SolarExposureWorker>(
                1, TimeUnit.DAYS,
                15, TimeUnit.MINUTES // Flex interval
            )
                .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
                .addTag("solar_exposure")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "solar_exposure_daily",
                ExistingPeriodicWorkPolicy.KEEP,
                solarWork
            )
            
            Timber.d("Solar exposure work scheduled successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule solar exposure work")
        }
    }
    
    fun cancelScheduledWork() {
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag("solar_exposure")
            Timber.d("Solar exposure work cancelled")
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel solar exposure work")
        }
    }
}
