package com.github.enteraname74.localdesktop.utils

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.localdesktop.tables.AlbumTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

/**
 * Utils related to the use of Exposed.
 */
object ExposedUtils {
    /**
     * Builds an Album from a ResultRow.
     */
    fun resultRowToAlbum(row: ResultRow): Album {
        return Album(
            albumId = UUID.fromString(row[AlbumTable.albumId]),
            albumName = row[AlbumTable.albumName],
            coverId = if (row[AlbumTable.coverId] == null) null else UUID.fromString(row[AlbumTable.coverId]),
            addedDate = row[AlbumTable.addedDate],
            nbPlayed = row[AlbumTable.nbPlayed],
            isInQuickAccess = row[AlbumTable.isInQuickAccess]
        )
    }
}