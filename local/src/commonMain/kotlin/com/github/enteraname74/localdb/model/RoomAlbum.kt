package com.github.enteraname74.localdb.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import com.github.enteraname74.localdb.model.cover.RoomSimpleCover
import com.github.enteraname74.localdb.model.cover.toRoomSimpleCover
import com.github.enteraname74.soulsearching.domain.model.Album
import com.github.enteraname74.soulsearching.domain.model.Scope
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
    @Embedded("cover_") val cover: RoomSimpleCover?,
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
    cover = cover?.toRoomSimpleCover(),
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    artistId = artist.artistId,
    remoteId = remoteId,
    lastUpdatedMillis = lastUpdateMillis,
    scope = scope,
)
