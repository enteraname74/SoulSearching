package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.soulsearching.ext.toWorkerResult
import com.github.enteraname74.soulsearching.coreui.strings.StringsUtils
import com.github.soulsearching.R
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class CloudSyncWorker(
    context: Context,
    parameters: WorkerParameters,
    private val syncDataWithCloudUseCase: SyncDataWithCloudUseCase,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result = coroutineScope {
        setForeground(setForegroundInfo(syncDataWithCloudUseCase.state.value))
        val foregroundUpdateJob = launch {
            syncDataWithCloudUseCase
                .state
                .drop(1)
                .collect { state ->
                    setForeground(setForegroundInfo(state))
                }
        }

        val result = try {
            syncDataWithCloudUseCase()
        } finally {
            foregroundUpdateJob.cancelAndJoin()
        }

        setForeground(setForegroundInfo(syncDataWithCloudUseCase.state.value))
        result.toWorkerResult()
    }

    private fun setForegroundInfo(
        state: SyncDataWithCloudUseCase.State,
    ) =
        if (Build.VERSION.SDK_INT >= 29) {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(state),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, createNotification(state))
        }

    private fun createNotification(
        state: SyncDataWithCloudUseCase.State,
    ): Notification {
        val strings = StringsUtils.getStrings(applicationContext)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(strings.cloudSyncNotificationTitle(state))
            .setContentText(strings.cloudSyncNotificationText(state))
            .setSmallIcon(R.drawable.app_logo_uni_xml)
            .apply {
                (state as? SyncDataWithCloudUseCase.State.ProgressState)?.progress?.let { progress ->
                    setProgress(100, (progress * 100).toInt(), false)
                }
            }
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= 29) {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(syncDataWithCloudUseCase.state.value),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, createNotification(syncDataWithCloudUseCase.state.value))
        }
    }

    companion object {
        const val CHANNEL_ID = "CloudSyncWorkerChannel"
        private const val NOTIFICATION_ID = 145
    }
}
