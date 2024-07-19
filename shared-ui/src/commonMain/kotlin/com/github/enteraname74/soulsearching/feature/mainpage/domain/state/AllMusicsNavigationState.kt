package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Music

sealed interface AllMusicsNavigationState {
    data object Idle: AllMusicsNavigationState
    data class ToModifyMusic(val selectedMusic: Music): AllMusicsNavigationState
}