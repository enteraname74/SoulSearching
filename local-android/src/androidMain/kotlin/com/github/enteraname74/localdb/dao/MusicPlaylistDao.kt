package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomMusicPlaylist
import java.util.*

/**
 * DAO of a MusicPlaylist.
 */
@Dao
internal interface MusicPlaylistDao {

    @Upsert
    suspend fun upsertMusicIntoPlaylist(roomMusicPlaylist: RoomMusicPlaylist)

    @Upsert
    suspend fun upsertAll(roomMusicPlaylists: List<RoomMusicPlaylist>)

    @Query("DELETE FROM RoomMusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID)

    @Query("DELETE FROM RoomMusicPlaylist WHERE dataMode = :dataMode")
    suspend fun deleteAll(dataMode: String)

    @Delete
    suspend fun deleteAll(roomMusicPlaylists: List<RoomMusicPlaylist>)

    @Delete
    suspend fun delete(roomMusicPlaylist: RoomMusicPlaylist)
}