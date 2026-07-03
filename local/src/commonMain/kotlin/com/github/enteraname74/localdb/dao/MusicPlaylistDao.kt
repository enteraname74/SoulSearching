package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomMusicPlaylist
import kotlin.uuid.Uuid

/**
 * DAO of a MusicPlaylist.
 */
@Dao
interface MusicPlaylistDao {

    @Upsert
    suspend fun upsertMusicIntoPlaylist(roomMusicPlaylist: RoomMusicPlaylist)

    @Query("DELETE FROM RoomMusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun deleteMusicFromPlaylist(musicId: Uuid, playlistId: Uuid)

    @Query("SELECT * FROM RoomMusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun getMusicPlaylist(musicId: Uuid, playlistId: Uuid): RoomMusicPlaylist?

    @Query("DELETE FROM RoomMusicPlaylist WHERE musicId = :musicId")
    suspend fun deleteMusicFromAllPlaylists(musicId: Uuid)
}
