package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain

import java.util.*

sealed interface SelectedMonthNavigationState {
    data object Idle: SelectedMonthNavigationState
    data class ToModifyMusic(val musicId: UUID): SelectedMonthNavigationState
    data object Back : SelectedMonthNavigationState
}