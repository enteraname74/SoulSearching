package com.github.enteraname74.localdb.dao

import androidx.room.Dao
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
    suspend fun insertMusicIntoPlaylist(roomMusicPlaylist: RoomMusicPlaylist)

    @Query("DELETE FROM RoomMusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID)

    @Query("SELECT * FROM RoomMusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): RoomMusicPlaylist?

    @Query("DELETE FROM RoomMusicPlaylist WHERE musicId = :musicId")
    suspend fun deleteMusicFromAllPlaylists(musicId: UUID)
}