package com.github.soulsearching.classes

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

object ColorPaletteUtils {
    fun getPaletteFromAlbumArt(albumArt: Bitmap?): Palette.Swatch? {
        if (albumArt == null) {
            Log.d("COLOR PALETTE UTILS", "NO BITMAP")
            return null
        }

        val palette = Palette.from(albumArt).generate()
        return if (palette.darkVibrantSwatch == null) {
            palette.dominantSwatch
        } else {
            palette.darkVibrantSwatch
        }
    }

    fun getDynamicPrimaryColor(): Color {
        return Color(
            ColorUtils.blendARGB(
                PlayerUtils.playerViewModel.currentColorPalette!!.rgb,
                Color.Black.toArgb(),
                0.5f
            )
        )
    }

    fun getDynamicSecondaryColor(): Color {
        return Color(
            ColorUtils.blendARGB(
                PlayerUtils.playerViewModel.currentColorPalette!!.rgb,
                Color.Black.toArgb(),
                0.2f
            )
        )
    }

    fun getTextColor(): Color {
        return Color(
            ColorUtils.blendARGB(
                Color.White.toArgb(),
                PlayerUtils.playerViewModel.currentColorPalette!!.rgb,
                0.1f
            )
        )
    }

    fun getOutlineTextColor(): Color {
        return Color(
            ColorUtils.blendARGB(
                Color.White.toArgb(),
                PlayerUtils.playerViewModel.currentColorPalette!!.rgb,
                0.3f
            )
        )
    }

}