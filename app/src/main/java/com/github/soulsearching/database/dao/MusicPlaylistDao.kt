package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicPlaylist
import java.util.*

@Dao
interface MusicPlaylistDao {

    @Upsert
    suspend fun insertMusicIntoPlaylist(musicPlaylist : MusicPlaylist)

    @Delete
    suspend fun deleteMusicFromPlaylist(musicPlaylist : MusicPlaylist)

    @Query("DELETE FROM MusicPlaylist WHERE musicId = :musicId")
    suspend fun deleteMusicFromAllPlaylists(musicId : UUID)

    @Query("SELECT * FROM MusicPlaylist WHERE musicId = :musicId AND playlistId = :playlistId")
    suspend fun checkIfMusicInPlaylist(musicId: UUID, playlistId : UUID) : MusicPlaylist?
}