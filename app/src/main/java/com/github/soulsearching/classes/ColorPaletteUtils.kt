package com.github.soulsearching.classes

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.palette.graphics.Palette

class ColorPaletteUtils {
    companion object {
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
    }
}