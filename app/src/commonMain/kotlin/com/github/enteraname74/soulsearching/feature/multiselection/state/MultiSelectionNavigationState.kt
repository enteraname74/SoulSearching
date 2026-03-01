package com.github.enteraname74.soulsearching.feature.multiselection.state

import java.util.UUID

sealed interface MultiSelectionNavigationState {
    data object Idle : MultiSelectionNavigationState

    data class ToMusicBottomSheet(
        val musicIds: List<UUID>,
        val playlistId: UUID?,
    ) : MultiSelectionNavigationState

    data class ToPlaylistBottomSheet(
        val playlistIds: List<UUID>,
    ) : MultiSelectionNavigationState

    data class ToArtistBottomSheet(
        val artistIds: List<UUID>,
    ) : MultiSelectionNavigationState

    data class ToAlbumBottomSheet(
        val albumIds: List<UUID>,
    ) : MultiSelectionNavigationState
}