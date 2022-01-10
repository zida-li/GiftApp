package dev.zidali.giftapp

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.HiltAndroidApp
import dev.zidali.giftapp.presentation.notification.NotificationHelper

@HiltAndroidApp
class BaseApplication: Application() {

    companion object {
        lateinit var instance: BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_HIGH, false,
            getString(R.string.app_name), "App notification channel.")
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_LOW, false,
            getString(R.string.month), "Notification channel for month")
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_HIGH, false,
            getString(R.string.week), "Notification channel for week")
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_MAX, false,
            getString(R.string.day), "Notification channel for day")
    }

}