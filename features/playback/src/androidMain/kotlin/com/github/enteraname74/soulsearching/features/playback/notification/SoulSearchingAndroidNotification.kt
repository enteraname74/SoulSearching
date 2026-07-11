package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.soulsearching.features.playback.PlayerLibraryService
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SoulSearchingAndroidNotification : SoulSearchingNotification, KoinComponent {
    private val mediaSessionManager: MediaSessionManager by inject()

    override suspend fun update(
        updateData: UpdateData,
    ) {
        withContext(Dispatchers.Main) {
            mediaSessionManager.updateExistingMediaSession(
                updateData = updateData,
            )
            PlayerLibraryService.triggerActiveNotificationUpdate()
        }
    }

    override fun dismiss(forceStop: Boolean) {
        if (forceStop) {
            mediaSessionManager.clearPlaybackState()
        }
    }
}
