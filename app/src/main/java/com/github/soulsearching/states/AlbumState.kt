package com.github.soulsearching.states

import android.graphics.Bitmap
import com.github.soulsearching.database.model.AlbumWithArtist

data class AlbumState(
    val albums: List<AlbumWithArtist> = emptyList(),
    val selectedAlbumWithArtist : AlbumWithArtist = AlbumWithArtist(),
    val cover : Bitmap? = null,
    val name : String = "",
    val artist : String = "",
    val isDeleteDialogShown: Boolean = false,
    val isBottomSheetShown: Boolean = false
)