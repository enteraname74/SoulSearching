package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

/**
 * Table for storing Artists.
 */
internal object ArtistTable: UUIDTable() {
    val artistName = varchar("artistName", 128)
    val coverId = uuid("coverId").nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
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
                Cover.FileCover(
                    fileCoverId = coverId,
                )
            },
            addedDate = this[ArtistTable.addedDate],
            nbPlayed = this[ArtistTable.nbPlayed],
            isInQuickAccess = this[ArtistTable.isInQuickAccess]
        )
    } catch (_: Exception) {
        null
    }