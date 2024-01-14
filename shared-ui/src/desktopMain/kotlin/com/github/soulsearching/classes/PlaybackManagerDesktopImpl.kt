package com.github.soulsearching.classes

import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.model.PlaybackManager

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

    override fun playNext() {

    }

    override fun playPrevious() {

    }

    override fun seekToPosition(position: Int) {

    }

    override fun getMusicDuration(): Int {
        return 0
    }

    override fun getCurrentMusicPosition(): Int {
        return 0
    }

    override fun stopMusic() {
    }

    override fun updateNotification() {
    }
}