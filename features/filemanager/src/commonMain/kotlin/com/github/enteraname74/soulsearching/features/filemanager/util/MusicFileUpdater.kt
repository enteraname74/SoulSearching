package com.github.enteraname74.soulsearching.features.filemanager.util

import com.github.enteraname74.domain.model.Music

/**
 * Utility class for updating a music file information.
 */
expect class MusicFileUpdater() {
    suspend fun updateMusic(music: Music)
}
