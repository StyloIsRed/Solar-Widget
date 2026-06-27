package com.stylo.solarwidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.stylo.solarwidget.R
import timber.log.Timber

class SolarWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.d("onUpdate called for ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.solar_widget)
        
        // Get data from SharedPreferences
        val prefs = context.getSharedPreferences("solar_prefs", Context.MODE_PRIVATE)
        val solarExposure = prefs.getInt("solar_exposure", 0)
        val cloudCoverage = prefs.getInt("cloud_coverage", 0)
        val lastUpdate = prefs.getLong("last_update", 0)
        
        views.setProgressBar(R.id.solar_progress, 100, solarExposure, false)
        views.setTextViewText(R.id.exposure_percentage, "$solarExposure%")
        views.setTextViewText(R.id.cloud_coverage, "Cloud: $cloudCoverage%")
        
        val lastUpdateText = if (lastUpdate > 0) {
            "Updated: ${java.text.SimpleDateFormat("HH:mm").format(lastUpdate)}"
        } else {
            "No data yet"
        }
        views.setTextViewText(R.id.last_update, lastUpdateText)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
        Timber.d("Widget $appWidgetId updated: solar=$solarExposure%, cloud=$cloudCoverage%")
    }
}
