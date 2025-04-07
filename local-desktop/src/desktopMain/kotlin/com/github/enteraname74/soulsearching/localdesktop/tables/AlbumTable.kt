package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Albums.
 */
internal object AlbumTable: UUIDTable() {
    val albumName = varchar("albumName", 128)
    val coverId = uuid("coverId").nullable()
    val coverUrl = mediumText("coverUrl").nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val artistId = reference("artistId", ArtistTable.id, ReferenceOption.CASCADE).index(
        customIndexName = "index_AlbumTable_artistId",
    )
    val dataMode = varchar("dataMode", 32).default(DataMode.Local.value)
}

private fun ResultRow.buildCover(): Cover? =
    if (this[AlbumTable.coverUrl] != null) {
        Cover.CoverUrl(
            url = this[AlbumTable.coverUrl],
        )
    } else {
        this[AlbumTable.coverId]?.let { coverId ->
            Cover.CoverFile(
                fileCoverId = coverId,
            )
        }
    }

/**
 * Builds an Album from a ResultRow.
 */
internal fun ResultRow.toAlbum(): Album? =
    try {
        Album(
            albumId = this[AlbumTable.id].value,
            albumName = this[AlbumTable.albumName],
            cover = buildCover(),
            addedDate = this[AlbumTable.addedDate],
            nbPlayed = this[AlbumTable.nbPlayed],
            isInQuickAccess = this[AlbumTable.isInQuickAccess],
            artistId = this[AlbumTable.artistId].value,
            dataMode = DataMode.fromString(
                this[AlbumTable.dataMode]
            ) ?: DataMode.Local,
        )
    } catch (_: Exception) {
        null
    }