package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.MusicAlbum
import com.github.soulsearching.database.model.MusicArtist
import java.util.UUID

@Dao
interface MusicArtistDao {
    @Upsert
    suspend fun insertMusicIntoArtist(musicArtist: MusicArtist)

    @Query("UPDATE MusicArtist SET artistId = :newArtistId WHERE musicId = :musicId")
    suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID)

    @Query("DELETE FROM MusicArtist WHERE musicId = :musicId")
    suspend fun deleteMusicFromArtist(musicId: UUID)
}