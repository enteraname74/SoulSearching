package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.MonthMusicList

/**
 * State for managing a selected month.
 */
data class SelectedMonthState(
    val monthMusicList: MonthMusicList? = MonthMusicList(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
)