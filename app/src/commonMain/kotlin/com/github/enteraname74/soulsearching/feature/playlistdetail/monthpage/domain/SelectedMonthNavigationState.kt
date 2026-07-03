package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain

import kotlin.uuid.Uuid

sealed interface SelectedMonthNavigationState {
    data object Idle: SelectedMonthNavigationState
    data class ToModifyMusic(val musicId: Uuid): SelectedMonthNavigationState
    data object Back : SelectedMonthNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : SelectedMonthNavigationState
}
