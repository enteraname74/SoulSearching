package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TransactionScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Cover.CoverFile.DevicePathSpec
import com.github.enteraname74.domain.model.Scope
import java.time.LocalDateTime
import java.util.*
import kotlin.uuid.Uuid

/**
 * Room representation of an Artist.
 */
@Entity
data class RoomArtist(
    @PrimaryKey
    val artistId: UUID = UUID.randomUUID(),
    val remoteId: Uuid?,
    val artistName: String,
    val coverId: UUID? = null,
    val coverFolderKey: String? = null,
    val coverUrl: String?,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long?,
    val scope: Scope,
)

/**
 * Converts a RoomArtist to an Artist.
 */
internal fun RoomArtist.toArtist(): Artist {
    val localCover = Cover.CoverFile(
        fileCoverId = coverId,
        devicePathSpec = coverFolderKey?.let { key ->
            DevicePathSpec(
                settingsKey = key,
                dynamicElementName = artistName,
                fallback = Cover.CoverFile(fileCoverId = coverId),
            )
        },
    )
    val remoteCover = coverUrl?.let { Cover.Url(it) }

    val usedCover = if (remoteCover == null) {
        localCover
    } else {
        localCover.takeIf { !it.isEmpty() } ?: remoteCover
    }

    return Artist(
        artistId = artistId,
        artistName = artistName,
        cover = usedCover,
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        remoteId = remoteId,
        lastUpdatedMillis = lastUpdatedMillis,
        scope = scope,
    )
}

/**
 * Converts an Artist to a RoomArtist.
 */
internal fun Artist.toRoomArtist(): RoomArtist = RoomArtist(
    artistId = artistId,
    artistName = artistName,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    coverFolderKey = (cover as? Cover.CoverFile)?.devicePathSpec?.settingsKey,
    remoteId = remoteId,
    lastUpdatedMillis = lastUpdatedMillis,
    coverUrl = (cover as? Cover.Url)?.url,
    scope = scope,
)