package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Artists.
 */
internal object ArtistTable: Table() {
    val artistId = varchar("artistId", 128)
    val artistName = varchar("artistName", 128)
    val coverId = varchar("coverId", 128).nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")

    override val primaryKey = PrimaryKey(artistId)
}