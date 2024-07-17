package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Music

sealed interface MainScreenNavigationState {
    data object Idle: MainScreenNavigationState
    data class ToModifyMusic(val selectedMusic: Music): MainScreenNavigationState
}