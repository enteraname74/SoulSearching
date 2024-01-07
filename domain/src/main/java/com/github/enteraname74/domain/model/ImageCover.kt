package com.github.enteraname74.domain.model

import android.graphics.Bitmap
import java.util.UUID

/**
 * Represent the cover of a song, album, artist or playlist.
 */
data class ImageCover(
    val id : Long = 0L,
    val coverId : UUID = UUID.randomUUID(),
    val cover : Bitmap? = null
)