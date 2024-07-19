package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Album

sealed interface AllAlbumsNavigationState {
    data object Idle: AllAlbumsNavigationState
    data class ToModifyAlbum(val selectedAlbum: Album): AllAlbumsNavigationState
}