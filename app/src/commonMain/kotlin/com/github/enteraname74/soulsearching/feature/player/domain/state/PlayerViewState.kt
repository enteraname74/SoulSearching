package com.github.enteraname74.soulsearching.feature.player.domain.state

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow

sealed interface PlayerViewState {
    data object Closed : PlayerViewState
    data class Data(
        val currentMusic: Music,
        val currentMusicIndex: Int,
        val isCurrentMusicInFavorite: Boolean,
        val playedList: Flow<PagingData<Music>>,
        val playerMode: PlayerMode,
        val isPlaying: Boolean,
        val playlistsWithMusics: List<PlaylistWithMusics>,
        val aroundSongs: List<Music?>,
        val initPlayerWithMinimiseView: Boolean,
    ): PlayerViewState
}
