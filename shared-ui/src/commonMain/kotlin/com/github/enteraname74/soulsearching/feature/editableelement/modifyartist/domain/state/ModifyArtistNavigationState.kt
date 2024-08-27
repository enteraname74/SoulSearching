package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state

sealed interface ModifyArtistNavigationState {
    data object Idle: ModifyArtistNavigationState
    data object Back: ModifyArtistNavigationState
}