package com.github.enteraname74.soulsearching.features.playback.environment

class NoOpPlaybackEnvironment : SoulSearchingPlaybackEnvironment {
    override suspend fun ensureReadyForPlayback(): Unit = Unit
    override fun release(): Unit = Unit
}
