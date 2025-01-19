package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.*

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
internal data class RoomMusicPlaylist(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val musicId: UUID = UUID.randomUUID(),
    @ColumnInfo(index = true) val playlistId: UUID = UUID.randomUUID(),
    val dataMode: String,
)

/**
 * Converts a RoomMusicPlaylist to a MusicPlaylist.
 */
internal fun RoomMusicPlaylist.toMusicPlaylist(): MusicPlaylist = MusicPlaylist(
    musicId = musicId,
    playlistId = playlistId,
    dataMode = DataMode.fromString(
        dataMode
    ) ?: DataMode.Local,
)

/**
 * Converts a MusicPlaylist to a RoomMusicPlaylist.
 */
internal fun MusicPlaylist.toRoomMusicPlaylist(): RoomMusicPlaylist = RoomMusicPlaylist(
    id = id,
    musicId = musicId,
    playlistId = playlistId,
    dataMode = dataMode.value,
)