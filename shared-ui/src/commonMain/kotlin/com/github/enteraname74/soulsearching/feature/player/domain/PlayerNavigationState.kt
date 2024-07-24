package com.github.enteraname74.soulsearching.feature.player.domain

import com.github.enteraname74.domain.model.Music

sealed interface PlayerNavigationState {
    data object Idle : PlayerNavigationState
    data class ToModifyMusic(val music: Music) : PlayerNavigationState
}