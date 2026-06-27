# Solar Widget - Android App

A smart Android widget that monitors solar exposure in your location and sends notifications based on cloud coverage patterns.

## Features

- ☀️ **Real-time Solar Exposure Tracking**: Monitor current solar exposure based on cloud coverage
- 📊 **Historical Data**: Stores daily solar exposure and cloud coverage data
- 🔔 **Smart Notifications**: Sends alerts at 6 AM if yesterday's cloud coverage exceeded 50%
- 🏠 **Home Screen Widget**: Quick glance at current solar exposure
- 📍 **Location-Based**: Uses your device's location for accurate data
- 🌙 **Background Monitoring**: Runs automatically without user intervention

## Prerequisites

- Android 8.0 (API 26) or higher
- Location permissions enabled
- Notification permissions enabled (Android 13+)
- Internet connection
- OpenWeatherMap API key (free tier available)

## Setup

### 1. Get API Key

1. Visit [OpenWeatherMap](https://openweathermap.org/api)
2. Sign up for a free account
3. Generate an API key
4. Add the key to `SharedPreferences` in the app:
   ```kotlin
   val prefs = context.getSharedPreferences("solar_prefs", Context.MODE_PRIVATE)
   prefs.edit().putString("weather_api_key", "YOUR_API_KEY").apply()
   ```

### 2. Add Widget to Home Screen

1. Long-press your home screen
2. Select "Widgets"
3. Find "Solar Exposure Widget"
4. Drag it to your home screen
5. Grant required permissions when prompted

### 3. Configure Notification

The app will automatically send notifications at 6 AM daily if yesterday's cloud coverage was > 50%.

## Architecture

### Technology Stack

- **Kotlin**: Main programming language
- **Room Database**: Local data persistence
- **Retrofit**: API calls to OpenWeatherMap
- **WorkManager**: Background task scheduling
- **Hilt**: Dependency injection
- **Coroutines**: Asynchronous operations
- **LiveData/Flow**: Reactive data streams

### Project Structure

```
app/src/main/java/com/stylo/solarwidget/
├── data/
│   ├── local/          # Room database entities and DAOs
│   ├── remote/         # Retrofit API interfaces
│   └── repository/     # Data repository layer
├── di/                 # Dependency injection modules
├── scheduler/          # WorkManager scheduling
├── widget/             # AppWidget provider
├── worker/             # Background work implementation
├── MainActivity.kt
└── SolarWidgetApp.kt
```

## Data Model

### SolarExposureEntity

```kotlin
data class SolarExposureEntity(
    val date: String,           // YYYY-MM-DD
    val cloudCoverage: Int,     // 0-100%
    val solarExposure: Int,     // 0-100% (100 - cloudCoverage)
    val timestamp: Long         // Milliseconds
)
```

## API Integration

The app uses OpenWeatherMap's Current Weather API:

```
GET https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}
```

## Background Scheduling

WorkManager is used to:
- Schedule daily checks at 6 AM
- Handle battery optimization
- Ensure reliability across device reboots
- Automatic retry on failures

## Permissions Required

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

## Future Enhancements

- [ ] Multiple location tracking
- [ ] Custom notification times
- [ ] Weather forecast integration
- [ ] UV index tracking
- [ ] Dark mode support
- [ ] Statistics dashboard
- [ ] Export historical data
- [ ] Custom alert thresholds

## Troubleshooting

### Widget not updating
1. Check if app has location permission
2. Verify internet connection
3. Ensure WorkManager is running

### Notifications not received
1. Check notification settings in system settings
2. Ensure "solar_channel" notification channel is enabled
3. Verify time zone in device settings

### Location not found
1. Enable GPS in device settings
2. Ensure "always" location permission is granted
3. Check if device has internet access

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
