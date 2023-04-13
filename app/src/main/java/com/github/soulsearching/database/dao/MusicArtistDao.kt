package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicAlbum
import com.github.soulsearching.database.model.MusicArtist
import java.util.UUID

@Dao
interface MusicArtistDao {
    @Upsert
    suspend fun insertMusicIntoArtist(musicArtist : MusicArtist)

    @Query("DELETE FROM MusicArtist WHERE musicId = :musicId")
    suspend fun deleteMusicFromArtist(musicId : UUID)
}