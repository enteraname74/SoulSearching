package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Scope
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

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
data class RoomAlbum(
    @PrimaryKey
    val albumId: Uuid = Uuid.random(),
    val remoteId: Uuid?,
    val albumName: String,
    val coverId: Uuid? = null,
    val coverUrl: String?,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    @ColumnInfo(index = true)
    val artistId: Uuid,
    val lastUpdatedMillis: Long?,
    val scope: Scope,
)

/**
 * Converts an Album to a RoomAlbum
 */
internal fun Album.toRoomAlbum(): RoomAlbum = RoomAlbum(
    albumId = albumId,
    albumName = albumName,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    artistId = artist.artistId,
    remoteId = remoteId,
    lastUpdatedMillis = lastUpdateMillis,
    coverUrl = (cover as? Cover.Url)?.url,
    scope = scope,
)
