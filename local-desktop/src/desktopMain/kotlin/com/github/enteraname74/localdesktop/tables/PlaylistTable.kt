package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Playlists.
 */
object PlaylistTable: Table() {
    val playlistId = varchar("playlistId", 128)
    var name = varchar("name", 128)
    var coverId = varchar("coverId", 128)
    val isFavorite = bool("isFavorite")
    var addedDate = datetime("addedDate")
    var nbPlayed = integer("nbPlayed")
    var isInQuickAccess = bool("isInQuickAccess")

    override val primaryKey = PrimaryKey(playlistId)
}