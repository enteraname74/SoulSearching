package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType

/**
 * Manage elements related to playlists.
 */
data class AllPlaylistsState(
    val playlists: List<PlaylistWithMusicsNumber> = emptyList(),
    var sortType: SortType = SortType.DEFAULT,
    var sortDirection: SortDirection = SortDirection.DEFAULT
)