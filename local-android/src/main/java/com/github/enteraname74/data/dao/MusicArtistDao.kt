package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomMusicArtist
import java.util.UUID

/**
 * DAO of a MusicArtist
 */
@Dao
internal interface MusicArtistDao {
    @Upsert
    suspend fun insertMusicIntoArtist(roomMusicArtist: RoomMusicArtist)

    @Query("UPDATE RoomMusicArtist SET artistId = :newArtistId WHERE musicId = :musicId")
    suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID)

    @Query("DELETE FROM RoomMusicArtist WHERE musicId = :musicId")
    suspend fun deleteMusicFromArtist(musicId: UUID)

    @Query("SELECT artistId FROM RoomMusicArtist WHERE musicId = :musicId")
    suspend fun getArtistIdFromMusicId(musicId: UUID) : UUID?

    @Query("SELECT COUNT(*) FROM RoomMusicArtist WHERE artistId = :artistId")
    suspend fun getNumberOfMusicsFromArtist(artistId : UUID) : Int
}