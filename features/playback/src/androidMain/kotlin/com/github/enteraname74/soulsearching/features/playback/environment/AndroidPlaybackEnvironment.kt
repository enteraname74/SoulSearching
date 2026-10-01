package com.github.enteraname74.soulsearching.features.playback.environment

class AndroidPlaybackEnvironment(
    private val mediaServiceConnector: MediaServiceConnector,
) : SoulSearchingPlaybackEnvironment {
    override suspend fun ensureReadyForPlayback() {
        mediaServiceConnector.ensureMediaServiceConnected()
    }

    override suspend fun release() {
        mediaServiceConnector.release()
    }
}
