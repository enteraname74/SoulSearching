package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithMusics

data class AlbumState(
    val albums: List<Album> = emptyList(),
    val selectedAlbum : AlbumWithMusics = AlbumWithMusics(),
    val cover : Bitmap? = null,
    val name : String = "",
    val artist : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false
)