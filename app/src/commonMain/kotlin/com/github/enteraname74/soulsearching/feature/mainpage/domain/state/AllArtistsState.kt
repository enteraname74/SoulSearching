package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Manage elements related to artists.
 */
data class AllArtistsState(
    val artists: Flow<PagingData<ArtistPreview>> = flowOf(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT,
)