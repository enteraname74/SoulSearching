package com.github.soulsearching.service.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

class NotificationMusicPlayer: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            MusicNotificationService.MUSIC_NOTIFICATION_CHANNEL_ID,
            "SoulSearchingNotification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Use for controlling the song that is currently playing"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}