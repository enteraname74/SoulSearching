package com.github.soulsearching.utils

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
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
        baseColor: Int?
    ): Color {
        return if (baseColor != null) blendARGB(
            baseColor,
            Color.Black.toArgb(),
            0.5f
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
        baseColor: Int?
    ): Color {
        return if (baseColor != null) blendARGB(
            baseColor,
            Color.Black.toArgb(),
            0.2f
        ) else {
            MaterialTheme.colorScheme.secondary
        }
    }

    /**
     * Blend two colors together.
     */
    private fun blendARGB(
        @ColorInt color1: Int, @ColorInt color2: Int,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Color {
        val firstColor = Color(color1)
        val secondColor = Color(color2)

        val inverseRatio = 1 - ratio
        val a = firstColor.alpha * inverseRatio + secondColor.alpha* ratio
        val r = firstColor.red * inverseRatio + secondColor.red * ratio
        val g = firstColor.green * inverseRatio + secondColor.green * ratio
        val b = firstColor.blue * inverseRatio + secondColor.blue * ratio
        return Color(
            red = r,
            green = g,
            blue = b,
            alpha = a
        )
    }
}