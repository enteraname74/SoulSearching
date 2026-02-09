package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import java.util.*

sealed interface SelectedAlbumNavigationState {
    data object Idle: SelectedAlbumNavigationState
    data class ToModifyMusic(val musicId: UUID): SelectedAlbumNavigationState
    data class ToEdit(val albumId: UUID): SelectedAlbumNavigationState
    data class ToArtist(val artistId: UUID): SelectedAlbumNavigationState
    data object Back: SelectedAlbumNavigationState
}