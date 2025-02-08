package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.enteraname74.domain.usecase.music.UploadAllMusicToCloudUseCase
import com.github.enteraname74.soulsearching.model.utils.StringsUtils
import com.github.soulsearching.R

class SettingsCloudUploadWorker(
    context: Context,
    parameters: WorkerParameters,
    private val uploadAllMusicToCloudUseCase: UploadAllMusicToCloudUseCase,
): CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        setForeground(setForegroundInfo(0f))
        uploadAllMusicToCloudUseCase(
            onProgressChanged = { progress ->
                setForeground(setForegroundInfo(progress))
            }
        )

        return Result.success()
    }

    private fun setForegroundInfo(progress: Float) =
        if (Build.VERSION.SDK_INT >= 29) {
            ForegroundInfo(NOTIFICATION_ID, createNotification(progress), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIFICATION_ID, createNotification(progress))
        }

    private fun createNotification(progress: Float): Notification {
        val strings = StringsUtils.getStrings(applicationContext)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(strings.cloudNotificationUploadTitle)
            .setContentText(strings.cloudNotificationUploadText)
            .setSmallIcon(R.drawable.ic_saxophone_svg)
            .setProgress(100, (progress * 100).toInt(), false)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= 29) {
            ForegroundInfo(NOTIFICATION_ID, createNotification(0f), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIFICATION_ID, createNotification(0f))
        }
    }

    companion object {
        const val CHANNEL_ID = "SoulSearchingCloudUploadNotificationChannel"
        private const val NOTIFICATION_ID = 145
    }
}