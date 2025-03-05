package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain

import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail

/**
 * State for managing a selected month.
 */
sealed interface SelectedMonthState {
    data object Error : SelectedMonthState
    data class Data(
        val playlistDetail: PlaylistDetail,
    ): SelectedMonthState
    data object Loading: SelectedMonthState
}