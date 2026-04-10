package com.github.enteraname74.soulsearching.feature.player.domain.state

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayerMode

sealed interface PlayerViewState {
    data object Closed : PlayerViewState
    data class Data(
        val currentMusic: Music,
        val currentMusicIndex: Int,
        val isCurrentMusicInFavorite: Boolean,
        val playedList: List<Music>,
        val playerMode: PlayerMode?,
        val isPlaying: Boolean,
        val playlistsWithMusics: List<PlaylistWithMusics>,
        val aroundSongs: List<Music>,
        val playedListScope: PlayedListScope,
        val sharedListState: SharedListState?,
    ) : PlayerViewState
}

data class SharedListState(
    val code: String,
    val host: User?,
    val guests: List<User>,
) {
    data class User(
        val username: String,
        val appearance: Int?,
    )
}
