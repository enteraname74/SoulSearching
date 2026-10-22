package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.localdb.model.cover.RoomSimpleCover
import com.github.enteraname74.localdb.model.cover.toRoomSimpleCover
import com.github.enteraname74.soulsearching.domain.model.Artist
import com.github.enteraname74.soulsearching.domain.model.Scope
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Room representation of an Artist.
 */
@Entity
data class RoomArtist(
    @PrimaryKey
    val artistId: Uuid = Uuid.random(),
    val remoteId: Uuid?,
    val artistName: String,
    @Embedded("cover_") val cover: RoomSimpleCover?,
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long?,
    val scope: Scope,
)

/**
 * Converts a RoomArtist to an Artist.
 */
internal fun RoomArtist.toArtist(): Artist =
    Artist(
        artistId = artistId,
        artistName = artistName,
        cover = cover?.toSimpleCover(artistName),
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        remoteId = remoteId,
        lastUpdatedMillis = lastUpdatedMillis,
        scope = scope,
    )

/**
 * Converts an Artist to a RoomArtist.
 */
internal fun Artist.toRoomArtist(): RoomArtist = RoomArtist(
    artistId = artistId,
    artistName = artistName,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    remoteId = remoteId,
    lastUpdatedMillis = lastUpdatedMillis,
    scope = scope,
    cover = cover?.toRoomSimpleCover(),
)
