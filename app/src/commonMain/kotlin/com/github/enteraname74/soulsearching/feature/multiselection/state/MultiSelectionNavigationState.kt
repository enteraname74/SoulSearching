package com.github.enteraname74.soulsearching.feature.multiselection.state

import kotlin.uuid.Uuid

sealed interface MultiSelectionNavigationState {
    data object Idle : MultiSelectionNavigationState

    data class ToMusicBottomSheet(
        val musicIds: List<Uuid>,
        val playlistId: Uuid?,
    ) : MultiSelectionNavigationState

    data class ToPlaylistBottomSheet(
        val playlistIds: List<Uuid>,
    ) : MultiSelectionNavigationState

    data class ToArtistBottomSheet(
        val artistIds: List<Uuid>,
    ) : MultiSelectionNavigationState

    data class ToAlbumBottomSheet(
        val albumIds: List<Uuid>,
    ) : MultiSelectionNavigationState
}
