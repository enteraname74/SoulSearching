package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortDirection
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortType

/**
 * Manage elements related to albums.
 */
data class AlbumState(
    val albums: List<AlbumWithArtist> = emptyList(),
    val cover : ImageBitmap? = null,
    val name : String = "",
    val artist : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC
)