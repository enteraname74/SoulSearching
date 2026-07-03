package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import kotlin.uuid.Uuid

sealed interface SelectedAlbumNavigationState {
    data object Idle: SelectedAlbumNavigationState
    data class ToModifyMusic(val musicId: Uuid): SelectedAlbumNavigationState
    data class ToEdit(val albumId: Uuid): SelectedAlbumNavigationState
    data class ToArtist(val artistId: Uuid): SelectedAlbumNavigationState
    data object Back: SelectedAlbumNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : SelectedAlbumNavigationState
}
