package com.github.enteraname74.soulsearching.feature.player.domain.state

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.domain.model.PlaylistWithMusics

sealed interface PlayerViewState {
    data object Closed : PlayerViewState
    data class Data(
        val currentMusic: Music,
        val currentMusicIndex: Int,
        val isCurrentMusicInFavorite: Boolean,
        val playedList: List<Music>,
        val playerMode: PlayerMode,
        val isPlaying: Boolean,
        val playlistsWithMusics: List<PlaylistWithMusics>,
        val aroundSongs: List<Music?>,
        val initPlayerWithMinimiseView: Boolean,
    ): PlayerViewState
}
