package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * UI State of the all musics tab on the main page.
 */
data class AllMusicsState(
    val musics: Flow<PagingData<Music>> = flowOf(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT,
    var monthMusicPreviews: List<MonthMusicsPreview> = listOf(),
)