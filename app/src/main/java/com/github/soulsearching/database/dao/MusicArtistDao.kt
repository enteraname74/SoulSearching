package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicAlbum
import com.github.soulsearching.database.model.MusicArtist

@Dao
interface MusicArtistDao {
    @Upsert
    suspend fun insertMusicIntoArtist(musicArtist : MusicArtist)

    @Delete
    suspend fun deleteMusicFromArtist(musicArtist : MusicArtist)
}