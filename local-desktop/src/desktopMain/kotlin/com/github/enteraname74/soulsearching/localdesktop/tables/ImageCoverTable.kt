package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.soulsearching.localdesktop.utils.ExposedUtils.stringToImageBitmap
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

/**
 * Table for storing ImageCovers.
 */
internal object ImageCoverTable : LongIdTable() {
    val coverId = varchar("coverId", 128)
    val cover = largeText("cover").nullable()
}

/**
 * Builds an ImageCover from a ResultRow.
 */
internal fun ResultRow.toImageCover(): ImageCover = ImageCover(
    id = this[ImageCoverTable.id].value,
    cover = stringToImageBitmap(this[ImageCoverTable.cover]),
    coverId = UUID.fromString(this[ImageCoverTable.coverId])
)