package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain

import com.github.enteraname74.domain.model.Music

sealed interface SelectedAlbumNavigationState {
    data object Idle: SelectedAlbumNavigationState
    data class ToModifyMusic(val music: Music): SelectedAlbumNavigationState
}