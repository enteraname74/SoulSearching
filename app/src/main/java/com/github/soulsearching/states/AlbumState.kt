package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType

/**
 * Manage elements related to albums.
 */
data class AlbumState(
    val albums: List<AlbumWithArtist> = emptyList(),
    val selectedAlbumWithArtist : AlbumWithArtist = AlbumWithArtist(),
    val cover : Bitmap? = null,
    val name : String = "",
    val artist : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    var sortType: Int = SortType.NAME,
    var sortDirection: Int = SortDirection.ASC
)