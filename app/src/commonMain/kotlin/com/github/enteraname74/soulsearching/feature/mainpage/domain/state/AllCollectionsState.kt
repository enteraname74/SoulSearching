package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class AllCollectionsState(
    val collections: Flow<PagingData<CollectionPreview>> = flowOf(),
    val sortType: SortType = SortType.DEFAULT,
    val sortDirection: SortDirection = SortDirection.DEFAULT,
)
