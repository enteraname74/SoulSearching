package com.github.soulsearching

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            SoulSearchingNotification.MUSIC_NOTIFICATION_CHANNEL_ID,
            "Soul Searching Music Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Use for controlling the song that is currently playing"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}