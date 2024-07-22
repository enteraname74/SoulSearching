package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Music

sealed interface SelectedArtistNavigationState {
    data object Idle: SelectedArtistNavigationState
    data class ToModifyAlbum(val album: Album): SelectedArtistNavigationState
    data class ToModifyMusic(val music: Music): SelectedArtistNavigationState
}