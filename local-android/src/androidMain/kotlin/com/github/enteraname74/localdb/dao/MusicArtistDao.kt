package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomMusicArtist
import java.util.UUID

/**
 * DAO of a MusicArtist
 */
@Dao
internal interface MusicArtistDao {
    @Query("SELECT * FROM RoomMusicArtist WHERE artistId = :artistId AND musicId = :musicId")
    suspend fun get(artistId: UUID, musicId: UUID): RoomMusicArtist?

    @Query("DELETE FROM RoomMusicArtist WHERE id = :id")
    suspend fun delete(
        id: String,
    )

    @Upsert
    suspend fun upsertMusicIntoArtist(roomMusicArtist: RoomMusicArtist)

    @Upsert
    suspend fun upsertAll(roomMusicArtists: List<RoomMusicArtist>)
}