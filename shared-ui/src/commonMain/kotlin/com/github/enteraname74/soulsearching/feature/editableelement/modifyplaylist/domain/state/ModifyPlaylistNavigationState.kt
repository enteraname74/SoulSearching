package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state

sealed interface ModifyPlaylistNavigationState {
    data object Idle: ModifyPlaylistNavigationState
    data object Back: ModifyPlaylistNavigationState
}