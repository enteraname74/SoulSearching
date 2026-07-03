package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import kotlin.uuid.Uuid

sealed interface SelectedArtistNavigationState {
    data object Idle : SelectedArtistNavigationState
    data class ToModifyAlbum(val albumId: Uuid) : SelectedArtistNavigationState
    data class ToModifyMusic(val musicId: Uuid) : SelectedArtistNavigationState
    data class ToAlbum(val albumId: Uuid) : SelectedArtistNavigationState
    data class ToEdit(val artistId: Uuid) : SelectedArtistNavigationState
    data object Back : SelectedArtistNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : SelectedArtistNavigationState
}
