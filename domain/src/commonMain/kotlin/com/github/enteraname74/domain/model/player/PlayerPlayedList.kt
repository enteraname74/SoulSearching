package com.github.enteraname74.domain.model.player

import kotlin.uuid.Uuid

data class PlayerPlayedList(
    val id: Uuid,
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

enum class PlayedListScope(
    val isRemote: Boolean,
    val isAdmin: Boolean,
) {
    LocalUser(
        isRemote = false,
        isAdmin = true,
    ),
    SharedHost(
        isRemote = true,
        isAdmin = true
    ),
    SharedGuest(
        isRemote = true,
        isAdmin = false,
    )
}

sealed interface PlayedListType {
    data class Shared(val invitationCode: String) : PlayedListType
    data object Local : PlayedListType
}
