package com.github.enteraname74.soulsearching.features.playback.environment

interface SoulSearchingPlaybackEnvironment {
    suspend fun ensureReadyForPlayback()
    suspend fun release()
}
