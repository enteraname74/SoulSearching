package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomMusicArtist
import java.util.UUID

/**
 * DAO of a MusicArtist
 */
@Dao
internal interface MusicArtistDao {
    @Upsert
    suspend fun upsertMusicIntoArtist(roomMusicArtist: RoomMusicArtist)

    @Query("UPDATE RoomMusicArtist SET artistId = :newArtistId WHERE musicId = :musicId")
    suspend fun updateArtistOfMusic(musicId: UUID, newArtistId: UUID)

    @Query("DELETE FROM RoomMusicArtist WHERE musicId = :musicId")
    suspend fun deleteMusicFromArtist(musicId: UUID)

    @Query("SELECT artistId FROM RoomMusicArtist WHERE musicId = :musicId")
    suspend fun getArtistIdFromMusicId(musicId: UUID) : UUID?
}