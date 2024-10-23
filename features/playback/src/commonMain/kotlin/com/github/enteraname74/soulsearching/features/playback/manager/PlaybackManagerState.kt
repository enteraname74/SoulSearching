package com.github.enteraname74.soulsearching.features.playback.manager

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode

sealed interface PlaybackManagerState {
    data object Stopped: PlaybackManagerState
    data class Data(
        val currentMusic: Music,
        val currentMusicIndex: Int,
        val playedList: List<Music>,
        val playerMode: PlayerMode,
        val isPlaying: Boolean,
        val minimisePlayer: Boolean,
    ): PlaybackManagerState {
        val currentMusicDuration: Int = currentMusic.duration.toInt()
    }
}