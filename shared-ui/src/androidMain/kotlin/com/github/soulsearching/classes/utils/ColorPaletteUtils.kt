package com.github.soulsearching.classes.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.runBlocking

/**
 * Utils for managing color palettes.
 */
object ColorPaletteUtils {
    /**
     * Tries to retrieve a palette from a potential image.
     */
    @Composable
    fun getPaletteFromAlbumArt(image: ImageBitmap?): Palette.Swatch? {
        if (image == null) {
            return null
        }
        val paletteState = rememberPaletteState()
        runBlocking {
            paletteState.generate(image)
        }
        return if (paletteState.palette?.darkVibrantSwatch == null) {
            paletteState.palette?.dominantSwatch
        } else {
            paletteState.palette?.darkVibrantSwatch
        }
    }

    /**
     * Retrieve the dynamic primary color from a color palette.
     * It will use the palette of the current played song by default.
     * If the palette is null, it will use the material theme.
     */
    @Composable
    fun getDynamicPrimaryColor(
        baseColor: Int? = PlayerUtils.playerViewModel.currentColorPalette?.rgb
    ): Color {
        return if (baseColor != null) Color(
            ColorUtils.blendARGB(
                baseColor,
                Color.Black.toArgb(),
                0.5f
            )
        ) else {
            MaterialTheme.colorScheme.primary
        }
    }

    /**
     * Retrieve the dynamic secondary color from a color palette.
     * It will use the palette of the current played song by default.
     * If the palette is null, it will use the material theme.
     */
    @Composable
    fun getDynamicSecondaryColor(
        baseColor: Int? = PlayerUtils.playerViewModel.currentColorPalette?.rgb
    ): Color {
        return if (baseColor != null) Color(
            ColorUtils.blendARGB(
                baseColor,
                Color.Black.toArgb(),
                0.2f
            )
        ) else {
            MaterialTheme.colorScheme.secondary
        }
    }
}