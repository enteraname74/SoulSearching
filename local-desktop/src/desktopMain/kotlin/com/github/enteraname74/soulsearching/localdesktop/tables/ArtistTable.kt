package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
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
    val coverFolderKey = varchar("coverFolderKey", 128).nullable()
}

/**
 * Builds an Artist from a ResultRow.
 */
internal fun ResultRow.toArtist(): Artist? =
    try {
        Artist(
            artistId = this[ArtistTable.id].value,
            artistName = this[ArtistTable.artistName],
            cover = buildCover(),
            addedDate = this[ArtistTable.addedDate],
            nbPlayed = this[ArtistTable.nbPlayed],
            isInQuickAccess = this[ArtistTable.isInQuickAccess],
        )
    } catch (_: Exception) {
        null
    }

private fun ResultRow.buildCover(): Cover? {
    val coverFolderKey: String? = this[ArtistTable.coverFolderKey]
    val coverId = this[ArtistTable.coverId]

    return Cover.CoverFile(
        fileCoverId = coverId,
        devicePathSpec = coverFolderKey?.let { key ->
            Cover.CoverFile.DevicePathSpec(
                settingsKey = key,
                dynamicElementName = this[ArtistTable.artistName],
                fallback = Cover.CoverFile(fileCoverId = coverId),
            )
        },
    )
}