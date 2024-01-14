package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * Table for storing ImageCovers.
 */
object ImageCover : LongIdTable() {
    val coverId = varchar("coverId", 128)
    val cover = largeText("cover")
}