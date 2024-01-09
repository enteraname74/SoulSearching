package com.github.soulsearching.states

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType

/**
 * Manage elements related to artists.
 */
data class ArtistState(
    val artists: List<ArtistWithMusics> = emptyList(),
    val selectedArtist : ArtistWithMusics = ArtistWithMusics(),
    val cover : ImageBitmap? = null,
    val name : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC
)