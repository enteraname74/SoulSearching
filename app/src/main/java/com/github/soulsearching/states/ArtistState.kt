package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType

/**
 * Manage elements related to artists.
 */
data class ArtistState(
    val artists: List<ArtistWithMusics> = emptyList(),
    val selectedArtist : ArtistWithMusics = ArtistWithMusics(),
    val cover : Bitmap? = null,
    val name : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC
)