package com.github.enteraname74.domain.model.player

import java.util.UUID

data class PlayerPlayedList(
    val id: UUID,
    val playlistId: String?,
    val isMainPlaylist: Boolean,
    val mode: PlayerMode,
    val state: PlayedListState,
    val type: PlayedListType,
    val scope: PlayedListScope,
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

enum class PlayedListScope {
    LocalUser,
    SharedHost,
    SharedGuest
}

sealed interface PlayedListType {
    data class Shared(val invitationCode: String): PlayedListType
    data object Local : PlayedListType
}