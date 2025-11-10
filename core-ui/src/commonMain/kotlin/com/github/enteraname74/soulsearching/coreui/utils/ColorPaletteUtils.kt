package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.ui.graphics.ImageBitmap
import com.kmpalette.palette.graphics.Palette
import com.kmpalette.PaletteState
import com.kmpalette.loader.ImageBitmapLoader
import kotlinx.coroutines.runBlocking

/**
 * Utils for managing color palettes.
 */
object ColorPaletteUtils {
    /**
     * Tries to retrieve a palette from a potential image.
     */
    fun getPaletteFromAlbumArt(image: ImageBitmap?): Palette.Swatch? {
        if (image == null) {
            return null
        }
        val paletteState = object : PaletteState<ImageBitmap>() {
            override val loader: ImageBitmapLoader<ImageBitmap>
                get() = object : ImageBitmapLoader<ImageBitmap> {
                    override suspend fun load(input: ImageBitmap): ImageBitmap = input
                }
        }
        runBlocking {
            paletteState.generate(image)
        }
        return if (paletteState.palette?.darkVibrantSwatch == null) {
            paletteState.palette?.dominantSwatch
        } else {
            paletteState.palette?.darkVibrantSwatch
        }
    }
}