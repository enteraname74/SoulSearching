package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

/**
 * Manage elements related to playlists.
 */
data class AllPlaylistsState(
    val playlists: List<PlaylistPreview> = emptyList(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT
)