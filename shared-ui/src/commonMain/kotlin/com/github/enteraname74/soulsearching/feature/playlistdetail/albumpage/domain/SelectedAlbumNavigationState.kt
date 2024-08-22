package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import com.github.enteraname74.domain.model.Music
import java.util.UUID

sealed interface SelectedAlbumNavigationState {
    data object Idle: SelectedAlbumNavigationState
    data class ToModifyMusic(val music: Music): SelectedAlbumNavigationState
    data object ToEdit: SelectedAlbumNavigationState
    data class ToArtist(val artistId: UUID): SelectedAlbumNavigationState
}