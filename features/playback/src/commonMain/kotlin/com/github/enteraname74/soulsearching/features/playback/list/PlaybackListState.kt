package com.github.enteraname74.soulsearching.features.playback.list

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerMode
import java.util.UUID

sealed interface PlaybackListState {
    data object NoData: PlaybackListState
    data class Data(
        val playedList: List<Music>,
        val initialList: List<Music>,
        val currentMusic: Music,
        val playerMode: PlayerMode,
        val isMainPlaylist: Boolean = false,
        val playlistId: UUID? = null,
        val minimisePlayer: Boolean = false,
    ): PlaybackListState {
        /**
         * Retrieve the index of the current played music.
         * Return -1 if the current music is null or if it is not found in the current playlist
         */
        val currentMusicIndex: Int
            get() = playedList.indexOf(playedList.find { it.musicId == currentMusic.musicId })
    }
}