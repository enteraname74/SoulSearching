package com.github.soulsearching.domain.utils

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
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
        println("trying to get palette")
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
            SoulSearchingColorTheme.defaultTheme.primary
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
            SoulSearchingColorTheme.defaultTheme.secondary
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