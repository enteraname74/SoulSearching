package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicAlbum

@Dao
interface MusicAlbumDao {
    @Upsert
    suspend fun insertMusicIntoAlbum(musicAlbum : MusicAlbum)

    @Delete
    suspend fun deleteMusicFromAlbum(musicAlbum : MusicAlbum)
}