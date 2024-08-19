package com.github.enteraname74.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

/**
 * Represent the cover of a song, album, artist or playlist.
 */
data class ImageCover(
    val id : Long = 0L,
    val coverId : UUID = UUID.randomUUID(),
    val cover : ImageBitmap? = null
)

fun List<ImageCover>.getFromCoverId(coverId: UUID?) : ImageBitmap? = firstOrNull { it.coverId == coverId }?.cover
