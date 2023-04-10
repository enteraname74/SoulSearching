package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicPlaylist
import java.util.*

@Dao
interface MusicPlaylistDao {

    @Upsert
    suspend fun insertMusicPlaylist(musicPlaylist : MusicPlaylist)

    @Query("DELETE FROM MusicPlaylist WHERE musicId = :musicId")
    suspend fun deleteMusicFromPlaylist(musicId : UUID)
}