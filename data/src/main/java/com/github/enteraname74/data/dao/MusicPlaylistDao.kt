package com.github.enteraname74.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.MusicPlaylist
import java.util.*

@Dao
internal interface MusicPlaylistDao {

    @Upsert
    suspend fun insertMusicIntoPlaylist(musicPlaylist : MusicPlaylist)

    @Query("DELETE FROM MusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID)

    @Query("DELETE FROM MusicPlaylist WHERE musicId = :musicId")
    suspend fun deleteMusicFromAllPlaylists(musicId : UUID)

    @Query("SELECT * FROM MusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun checkIfMusicInPlaylist(musicId: UUID, playlistId : UUID) : MusicPlaylist?
}