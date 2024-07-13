package com.github.enteraname74.soulsearching.model

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.feature.player.domain.model.SoulSearchingPlayer

class SoulSearchingDesktopPlayerImpl: SoulSearchingPlayer {
    override fun init() {

    }

    override fun setMusic(music: Music) {

    }

    override fun onlyLoadMusic(seekTo: Int) {

    }

    override fun launchMusic() {

    }

    override fun togglePlayPause() {

    }

    override fun play() {

    }

    override fun pause() {

    }

    override fun seekToPosition(position: Int) {

    }

    override fun isPlaying(): Boolean {
        return false
    }

    override fun dismiss() {

    }

    override fun getMusicPosition(): Int {
        return 0
    }

    override fun getMusicDuration(): Int {
        return 0
    }
}