package com.github.soulsearching.classes

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.palette.graphics.Palette

class ColorPaletteUtils {
    companion object {
        fun getPaletteFromAlbumArt(albumArt: Bitmap?, context: Context): Palette.Swatch? {
            if (albumArt == null) {
                return null
            }

            val palette = Palette.from(albumArt).generate()
            Log.d("COLOR UTILS", "SE")
            return if (palette.darkVibrantSwatch == null) {
                palette.dominantSwatch
            } else {
                palette.darkVibrantSwatch
            }
        }
    }
}