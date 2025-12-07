package com.github.enteraname74.soulsearching.theme

import androidx.compose.ui.graphics.ImageBitmap

sealed interface PlaylistDetailCover {
    data object NoCover : PlaylistDetailCover
    data class Cover(val playlistImage: ImageBitmap) : PlaylistDetailCover

    companion object {
        fun fromImageBitmap(imageBitmap: ImageBitmap?): PlaylistDetailCover =
            if (imageBitmap == null) {
                NoCover
            } else {
                Cover(playlistImage = imageBitmap)
            }
    }
}