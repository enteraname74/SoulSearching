package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Music
import java.util.UUID

sealed interface SelectedArtistNavigationState {
    data object Idle: SelectedArtistNavigationState
    data class ToModifyAlbum(val album: Album): SelectedArtistNavigationState
    data class ToModifyMusic(val music: Music): SelectedArtistNavigationState
    data class ToAlbum(val albumId: UUID): SelectedArtistNavigationState
    data object ToEdit: SelectedArtistNavigationState
}