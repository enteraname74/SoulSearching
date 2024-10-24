package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Musics.
 */
internal object MusicTable : UUIDTable() {
    val name = varchar("name", 128)
    val album = varchar("album", 128)
    val artist = varchar("artist", 128)
    val coverId = uuid("coverId").nullable()
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
            cover = Cover.CoverFile(
                fileCoverId = this[MusicTable.coverId],
                initialCoverPath = this[MusicTable.path],
            ),
            duration = this[MusicTable.duration],
            path = this[MusicTable.path],
            folder = this[MusicTable.folder],
            addedDate = this[MusicTable.addedDate],
            nbPlayed = this[MusicTable.nbPlayed],
            isInQuickAccess = this[MusicTable.isInQuickAccess],
            isHidden = this[MusicTable.isHidden]
        )
    } catch (e: Exception) {
        null
    }