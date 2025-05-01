package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state

sealed interface ModifyMusicNavigationState {
    data object Idle: ModifyMusicNavigationState
    data object Back: ModifyMusicNavigationState
}