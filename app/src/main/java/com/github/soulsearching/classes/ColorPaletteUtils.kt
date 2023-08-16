package com.github.soulsearching.classes

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.ColorInt
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

    fun getDynamicPrimaryColor(
        baseColor: Int = PlayerUtils.playerViewModel.currentColorPalette!!.rgb
    ): Color {
        return Color(
            ColorUtils.blendARGB(
                baseColor,
                Color.Black.toArgb(),
                0.5f
            )
        )
    }

    fun getDynamicSecondaryColor(
        baseColor: Int = PlayerUtils.playerViewModel.currentColorPalette!!.rgb
    ): Color {
        return Color(
            ColorUtils.blendARGB(
                baseColor,
                Color.Black.toArgb(),
                0.2f
            )
        )
    }
}