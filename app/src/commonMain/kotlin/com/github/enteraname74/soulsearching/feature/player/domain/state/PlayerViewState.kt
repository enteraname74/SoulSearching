package com.github.enteraname74.soulsearching.feature.player.domain.state

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState

sealed interface PlayerViewState {
    data object Closed : PlayerViewState
    data class Data(
        val currentMusic: Music,
        val artistsOfCurrentMusic: List<Artist>,
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
