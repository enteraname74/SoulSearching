package com.github.enteraname74.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.ImageCover
import java.util.UUID

/**
 * Room representation of an ImageCover.
 */
@Entity
internal data class RoomImageCover(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val coverId: UUID = UUID.randomUUID(),
    val cover: Bitmap? = null
)

/**
 * Converts a RoomImageCover to an ImageCover.
 */
internal fun RoomImageCover.toImageCover(): ImageCover = ImageCover(
    id = id,
    coverId = coverId,
    cover = cover
)

/**
 * Converts an ImageCover to a RoomImageCover.
 */
internal fun ImageCover.toRoomImageCover(): RoomImageCover = RoomImageCover(
    id = id,
    coverId = coverId,
    cover = cover
)