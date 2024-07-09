package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of an Artist.
 */
@Dao
internal interface ArtistDao {

    @Upsert
    suspend fun upsert(roomArtist: RoomArtist)

    @Delete
    suspend fun delete(roomArtist: RoomArtist)

    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getFromId(artistId: UUID): Flow<RoomArtist?>

    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAll(): Flow<List<RoomArtist>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAllArtistWithMusics(): Flow<List<RoomArtistWithMusics?>>

    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName LIMIT 1")
    suspend fun getFromName(artistName: String): RoomArtist?

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId: UUID): Flow<RoomArtistWithMusics?>
}