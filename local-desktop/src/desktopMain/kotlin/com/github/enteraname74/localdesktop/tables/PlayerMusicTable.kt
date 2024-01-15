package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Table for storing PlayerMusics.
 */
object PlayerMusicTable: LongIdTable() {
    val musicId = reference("musicId", MusicTable.musicId, onDelete = ReferenceOption.CASCADE)
}