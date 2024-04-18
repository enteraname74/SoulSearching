package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Musics.
 */
internal object MusicTable: Table() {
    val musicId = varchar("musicId", 128)
    val name = varchar("name", 128)
    val album = varchar("album", 128)
    val artist = varchar("artist", 128)
    val coverId = varchar("coverId", 128).nullable()
    val duration = long("duration")
    val path = mediumText("path")
    val folder = varchar("folder", 128)
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val isHidden = bool("isHidden")

    override val primaryKey = PrimaryKey(musicId)
}