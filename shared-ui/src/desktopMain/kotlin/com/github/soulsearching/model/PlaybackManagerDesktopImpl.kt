package com.github.soulsearching.model

import com.github.enteraname74.domain.model.Music

/**
 * Class managing playback on desktop.
 */
class PlaybackManagerDesktopImpl: PlaybackManager {
    override fun initializePlayerFromSavedList(savedMusicList: ArrayList<Music>) {

    }

    override fun initializeMusicPlayerManager(isFromSavedList: Boolean) {

    }

    override fun setAndPlayCurrentMusic() {

    }

    override fun onlyLoadMusic() {

    }

    override fun isPlayerPlaying(): Boolean {
        return false
    }

    override fun togglePlayPause() {

    }

    override fun next() {

    }

    override fun previous() {

    }

    override fun seekToPosition(position: Int) {

    }

    override fun getMusicDuration(): Int {
        return 0
    }

    override fun getCurrentMusicPosition(): Int {
        return 0
    }

    override fun stopPlayback() {
    }

    override fun updateNotification() {
    }
}