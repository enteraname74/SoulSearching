package com.github.enteraname74.localdb.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.MusicPlaylist
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
    ]
)
data class RoomMusicPlaylist(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val musicId: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val playlistId: Uuid = Uuid.random()
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
