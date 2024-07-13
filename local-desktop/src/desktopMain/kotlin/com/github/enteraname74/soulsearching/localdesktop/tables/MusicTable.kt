package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Music
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

/**
 * Table for storing Musics.
 */
internal object MusicTable : UUIDTable() {
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
}

/**
 * Builds a Music from a ResultRow.
 */
internal fun ResultRow.toMusic(): Music? =
    try {
        Music(
            musicId = this[MusicTable.id].value,
            name = this[MusicTable.name],
            album = this[MusicTable.album],
            artist = this[MusicTable.artist],
            coverId = this[MusicTable.coverId]?.let { UUID.fromString(it) },
            duration = this[MusicTable.duration],
            path = this[MusicTable.path],
            folder = this[MusicTable.folder],
            addedDate = this[MusicTable.addedDate],
            nbPlayed = this[MusicTable.nbPlayed],
            isInQuickAccess = this[MusicTable.isInQuickAccess],
            isHidden = this[MusicTable.isHidden]
        )
    } catch (e: Exception) {
//        println("Got exception!: $e")
//        this.fieldIndex.forEach { (t, u) -> println("$t, $u") }
//        println("ID: ${this[MusicTable.id]}")
        null
    }