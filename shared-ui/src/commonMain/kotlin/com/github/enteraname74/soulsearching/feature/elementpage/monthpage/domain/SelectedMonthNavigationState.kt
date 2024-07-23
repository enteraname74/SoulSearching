package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import com.github.enteraname74.domain.model.Music

sealed interface SelectedMonthNavigationState {
    data object Idle: SelectedMonthNavigationState
    data class ToModifyMusic(val music: Music): SelectedMonthNavigationState
}