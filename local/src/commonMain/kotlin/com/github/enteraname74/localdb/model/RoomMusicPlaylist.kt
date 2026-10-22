package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.github.enteraname74.soulsearching.domain.model.MusicPlaylist
import kotlin.uuid.Uuid

/**
 * Room representation of a MusicPlaylist.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomMusic::class,
            parentColumns = ["musicId"],
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomPlaylist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["playlistId", "musicId"], unique = true),
        Index(value = ["musicId"]),
    ],
)
data class RoomMusicPlaylist(
    @PrimaryKey val id: String,
    val musicId: Uuid = Uuid.random(),
    val playlistId: Uuid = Uuid.random()
)

/**
 * Converts a RoomMusicPlaylist to a MusicPlaylist.
 */
internal fun RoomMusicPlaylist.toMusicPlaylist(): MusicPlaylist = MusicPlaylist(
    musicId = musicId,
    playlistId = playlistId
)

/**
 * Converts a MusicPlaylist to a RoomMusicPlaylist.
 */
internal fun MusicPlaylist.toRoomMusicPlaylist(): RoomMusicPlaylist = RoomMusicPlaylist(
    id = id,
    musicId = musicId,
    playlistId = playlistId
)
