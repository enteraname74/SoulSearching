package com.github.enteraname74.domain.model.player

import java.util.UUID

data class PlayerPlayedList(
    val id: UUID,
    val playlistId: UUID?,
    val isMainPlaylist: Boolean,
    val mode: PlayerMode,
    val state: PlayedListState,
) {
    fun getOrder(music: PlayerMusic): Double =
        if (mode == PlayerMode.Shuffle) music.shuffledOrder else music.order
}

enum class PlayedListState {
    Playing,
    Paused,
    Loading,
    Cached
}