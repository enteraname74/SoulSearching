package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.default
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Artists.
 */
internal object ArtistTable: UUIDTable() {
    val artistName = varchar("artistName", 128)
    val coverId = uuid("coverId").nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val dataMode = varchar("dataMode", 32).default(DataMode.Local.value)
}

/**
 * Builds an Artist from a ResultRow.
 */
internal fun ResultRow.toArtist(): Artist? =
    try {
        Artist(
            artistId = this[ArtistTable.id].value,
            artistName = this[ArtistTable.artistName],
            cover = this[ArtistTable.coverId]?.let { coverId ->
                Cover.CoverFile(
                    fileCoverId = coverId,
                )
            },
            addedDate = this[ArtistTable.addedDate],
            nbPlayed = this[ArtistTable.nbPlayed],
            isInQuickAccess = this[ArtistTable.isInQuickAccess],
            dataMode = DataMode.fromString(
                this[ArtistTable.dataMode]
            ) ?: DataMode.Local,
        )
    } catch (_: Exception) {
        null
    }