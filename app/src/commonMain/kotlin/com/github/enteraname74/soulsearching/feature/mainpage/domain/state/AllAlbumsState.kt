package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

/**
 * Manage elements related to albums.
 */
data class AllAlbumsState(
    val albums: List<AlbumWithMusics> = emptyList(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT,
)