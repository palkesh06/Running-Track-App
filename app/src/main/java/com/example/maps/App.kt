package com.example.maps

import android.app.Application
import com.example.maps.background.tracking.service.notification.TrackingNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var notificationHelper: TrackingNotificationHelper
    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel()
    }
}