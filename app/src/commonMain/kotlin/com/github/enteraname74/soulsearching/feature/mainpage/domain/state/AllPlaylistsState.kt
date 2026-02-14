package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Manage elements related to playlists.
 */
data class AllPlaylistsState(
    val playlists: Flow<PagingData<PlaylistPreview>> = flowOf(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT
)