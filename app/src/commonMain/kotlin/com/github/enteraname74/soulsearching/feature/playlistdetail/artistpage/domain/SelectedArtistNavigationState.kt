package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import java.util.*

sealed interface SelectedArtistNavigationState {
    data object Idle : SelectedArtistNavigationState
    data class ToModifyAlbum(val albumId: UUID) : SelectedArtistNavigationState
    data class ToModifyMusic(val musicId: UUID) : SelectedArtistNavigationState
    data class ToAlbum(val albumId: UUID) : SelectedArtistNavigationState
    data class ToEdit(val artistId: UUID) : SelectedArtistNavigationState
    data object Back : SelectedArtistNavigationState
}