package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicAlbum
import java.util.UUID

@Dao
interface MusicAlbumDao {
    @Upsert
    suspend fun insertMusicIntoAlbum(musicAlbum : MusicAlbum)

    @Query("DELETE FROM MusicAlbum WHERE musicId = :musicId")
    suspend fun deleteMusicFromAlbum(musicId : UUID)
}