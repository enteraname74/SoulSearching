package com.github.soulsearching.classes

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

object ColorPaletteUtils {
    fun getPaletteFromAlbumArt(albumArt: Bitmap?): Palette.Swatch? {
        if (albumArt == null) {
            return null
        }

        val palette = Palette.from(albumArt).generate()
        return if (palette.darkVibrantSwatch == null) {
            palette.dominantSwatch
        } else {
            palette.darkVibrantSwatch
        }
    }

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