package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.*

/**
 * UI State of the all musics tab on the main page.
 */
data class AllMusicsState(
    val musics: List<Music> = listOf(),
    var sortType: Int = SortDirection.ASC,
    var sortDirection: Int = SortDirection.ASC,
    var monthMusics: List<MonthMusics> = listOf(),
)