package com.stylo.solarwidget.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.stylo.solarwidget.R
import com.stylo.solarwidget.data.repository.SolarExposureRepository
import dagger.hilt.android.HiltWorker
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltWorker
class SolarExposureWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val repository: SolarExposureRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            Timber.d("SolarExposureWorker: Starting daily check")
            
            // Get user's location
            val location = try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
                val lastLocation = fusedLocationClient.lastLocation.await()
                lastLocation
            } catch (e: SecurityException) {
                Timber.e(e, "Location permission not granted")
                null
            }
            
            if (location != null) {
                Timber.d("Location obtained: ${location.latitude}, ${location.longitude}")
                // Update solar exposure data
                repository.updateSolarExposure(location.latitude, location.longitude)
                
                // Check if we should notify
                if (repository.shouldNotifyUser()) {
                    sendNotification()
                }
            } else {
                Timber.w("Could not obtain user location")
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SolarExposureWorker failed")
            Result.retry()
        }
    }
    
    private fun sendNotification() {
        try {
            val notification = NotificationCompat.Builder(applicationContext, "solar_channel")
                .setContentTitle("☀️ Low Solar Exposure Yesterday")
                .setContentText("Cloud coverage was >50% yesterday")
                .setSmallIcon(R.drawable.ic_sun)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            
            NotificationManagerCompat.from(applicationContext).notify(1, notification)
            Timber.d("Notification sent")
        } catch (e: Exception) {
            Timber.e(e, "Failed to send notification")
        }
    }
}
