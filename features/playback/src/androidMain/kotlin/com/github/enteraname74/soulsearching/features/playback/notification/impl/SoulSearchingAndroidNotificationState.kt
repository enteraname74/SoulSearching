package com.github.enteraname74.soulsearching.features.playback.notification.impl

import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music

sealed interface SoulSearchingAndroidNotificationState {
    data object Inactive: SoulSearchingAndroidNotificationState
    data class Active(
        val music: Music,
        val cover: ImageBitmap?,
        val isPlaying: Boolean,
        val mediaSessionToken: MediaSessionCompat.Token,
    ): SoulSearchingAndroidNotificationState
}