package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Albums.
 */
object AlbumTable: Table() {
    val albumId = varchar("albumId", 128)
    val albumName = varchar("albumName", 128)
    val coverId = varchar("albumName", 128)
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")

    override val primaryKey = PrimaryKey(albumId)
}