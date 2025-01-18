package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import org.springframework.security.crypto.keygen.KeyGenerators.string

/**
 * Table for storing Musics.
 */
internal object MusicTable : UUIDTable() {
    val name = varchar("name", 128)
    val album = varchar("album", 128)
    val artist = varchar("artist", 128)
    val coverId = uuid("coverId").nullable()
    val coverUrl = mediumText("coverUrl").nullable()
    val duration = long("duration")
    val path = mediumText("path")
    val folder = varchar("folder", 128)
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val isHidden = bool("isHidden")
    val dataMode = varchar("dataMode", 32).default(DataMode.Local.value)
    val albumId = reference("albumId", AlbumTable.id, ReferenceOption.CASCADE).index(
        customIndexName = "index_MusicTable_albumId"
    )
}

private fun ResultRow.buildCover(): Cover =
    if (this[MusicTable.coverUrl] != null) {
        Cover.CoverUrl(
            url = this[MusicTable.coverUrl],
        )
    } else {
        Cover.CoverFile(
            fileCoverId = this[MusicTable.coverId],
            initialCoverPath = this[MusicTable.path],
        )
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
            cover = buildCover(),
            duration = this[MusicTable.duration],
            path = this[MusicTable.path],
            folder = this[MusicTable.folder],
            addedDate = this[MusicTable.addedDate],
            nbPlayed = this[MusicTable.nbPlayed],
            isInQuickAccess = this[MusicTable.isInQuickAccess],
            isHidden = this[MusicTable.isHidden],
            dataMode = DataMode.fromString(this[MusicTable.dataMode]) ?: DataMode.Local,
            albumId = this[MusicTable.albumId].value,
        )
    } catch (e: Exception) {
        null
    }