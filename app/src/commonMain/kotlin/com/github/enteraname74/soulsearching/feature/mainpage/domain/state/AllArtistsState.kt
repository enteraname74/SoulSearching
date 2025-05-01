package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

/**
 * Manage elements related to artists.
 */
data class AllArtistsState(
    val artists: List<ArtistWithMusics> = emptyList(),
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC
)