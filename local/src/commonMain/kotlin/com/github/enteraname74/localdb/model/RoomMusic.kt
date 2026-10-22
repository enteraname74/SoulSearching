package com.github.enteraname74.localdb.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import com.github.enteraname74.localdb.model.cover.RoomSimpleCover
import com.github.enteraname74.localdb.model.cover.toRoomSimpleCover
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.Scope
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
    @Embedded("cover_") val cover: RoomSimpleCover,
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
    scope = scope,
    cover = cover.toRoomSimpleCover(),
)
