package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SoulSearchingDesktopNotification(
    workDispatcher: WorkDispatcher,
) : SoulSearchingNotification, KoinComponent {
    private val playbackManager: PlaybackManager by inject()
    private val mprisMediaSession: MprisMediaSession by lazy {
        MprisMediaSession(
            playbackManager = playbackManager,
            workDispatcher = workDispatcher,
        )
    }

    override suspend fun update(updateData: UpdateData) {
        mprisMediaSession.update(
            updateData = updateData,
            // TODO DESKTOP: Add support for cover
            artUrl = null,
        )
    }

    override fun dismiss(forceStop: Boolean) {
        mprisMediaSession.dismiss()
    }
}
