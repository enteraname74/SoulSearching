package com.github.enteraname74.localdb.dao

import androidx.room3.*
import com.github.enteraname74.localdb.model.RoomMusicArtist
import kotlin.uuid.Uuid

/**
 * DAO of a MusicArtist
 */
@Dao
interface MusicArtistDao {
    @Query("SELECT * FROM RoomMusicArtist WHERE artistId = :artistId AND musicId = :musicId")
    suspend fun get(artistId: Uuid, musicId: Uuid): RoomMusicArtist?

    @Query("DELETE FROM RoomMusicArtist WHERE artistId = :artistId")
    suspend fun deleteOfArtist(artistId: Uuid)

    @Query("DELETE FROM RoomMusicArtist WHERE musicId = :musicId")
    suspend fun deleteOfMusic(musicId: Uuid)

    @Query("DELETE FROM RoomMusicArtist WHERE id = :id")
    suspend fun delete(
        id: String,
    )

    @Upsert
    suspend fun upsertMusicIntoArtist(roomMusicArtist: RoomMusicArtist)

    @Upsert
    suspend fun upsertAll(roomMusicArtists: List<RoomMusicArtist>)
}
