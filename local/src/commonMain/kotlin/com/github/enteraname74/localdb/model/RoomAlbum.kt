package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Scope
import java.time.LocalDateTime
import java.util.UUID
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
    val albumId: UUID = UUID.randomUUID(),
    val remoteId: Uuid?,
    val albumName: String,
    val coverId: UUID? = null,
    val coverUrl: String?,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    @ColumnInfo(index = true)
    val artistId: UUID,
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