package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Room representation of a song.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomAlbum::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class RoomMusic(
    @PrimaryKey
    val musicId: Uuid = Uuid.random(),
    val remoteId: String?,
    val lastUpdateMillis: Long?,
    var name: String = "",
    var coverId: Uuid? = null,
    val coverUrl: String?,
    var duration: Long = 0L,
    val path: String?,
    var localPath: String?,
    val remotePath: String?,
    var folder: String = "",
    var addedDate: Instant = Clock.System.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    var isHidden: Boolean = false,
    var albumPosition: Int?,
    @ColumnInfo(index = true)
    val albumId: Uuid,
    val scope: Scope,
)

/**
 * Converts a Music to a RoomMusic.
 */
internal fun Music.toRoomMusic(): RoomMusic = RoomMusic(
    musicId = musicId,
    name = name,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    duration = duration,
    localPath = localPath,
    folder = folder,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    isHidden = isHidden,
    albumPosition = albumPosition,
    albumId = album.albumId,
    remoteId = remoteId,
    lastUpdateMillis = lastUpdatedMillis,
    remotePath = remotePath,
    path = path,
    coverUrl = (cover as? Cover.Url)?.url,
    scope = scope,
)
