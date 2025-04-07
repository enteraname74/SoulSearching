package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room representation of an Album.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomArtist::class,
            parentColumns = ["artistId"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
internal data class RoomAlbum(
    @PrimaryKey
    val albumId: UUID = UUID.randomUUID(),
    var albumName: String = "",
    var coverId: UUID? = null,
    var coverUrl: UUID? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    @ColumnInfo(index = true)
    val artistId: UUID,
    val dataMode: String = DataMode.Local.value,
) {
    fun buildCover(): Cover? =
        if (coverUrl != null) {
            Cover.CoverUrl(
                url = coverUrl
            )
        } else {
            coverId?.let {
                Cover.CoverFile(
                    fileCoverId = it,
                )
            }
        }
}

/**
 * Converts a RoomAlbum to an Album.
 */
internal fun RoomAlbum.toAlbum(): Album = Album(
    albumId = albumId,
    albumName = albumName,
    cover = buildCover(),
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    artistId = artistId,
    dataMode = DataMode.fromString(dataMode) ?: DataMode.Local,
)

/**
 * Converts an Album to a RoomAlbum
 */
internal fun Album.toRoomAlbum(): RoomAlbum = RoomAlbum(
    albumId = albumId,
    albumName = albumName,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    coverUrl = (cover as? Cover.CoverUrl)?.url,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    artistId = artistId,
    dataMode = dataMode.value,
)