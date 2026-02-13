package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Manage elements related to albums.
 */
data class AllAlbumsState(
    val albums: Flow<PagingData<AlbumPreview>> = flowOf(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT,
)