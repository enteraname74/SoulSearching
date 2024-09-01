package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state

sealed interface ModifyAlbumNavigationState {
    data object Idle: ModifyAlbumNavigationState
    data object Back: ModifyAlbumNavigationState
}