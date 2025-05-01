package com.github.enteraname74.soulsearching.theme

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.soulsearching.coreui.utils.ColorPaletteUtils

sealed interface PlaylistDetailCover {
    data object NoCover : PlaylistDetailCover
    data class Cover(val playlistImage: ImageBitmap) : PlaylistDetailCover {
        val palette = ColorPaletteUtils.getPaletteFromAlbumArt(
            image = playlistImage
        )
    }

    companion object {
        fun fromImageBitmap(imageBitmap: ImageBitmap?): PlaylistDetailCover =
            if (imageBitmap == null) {
                NoCover
            } else {
                Cover(playlistImage = imageBitmap)
            }
    }
}