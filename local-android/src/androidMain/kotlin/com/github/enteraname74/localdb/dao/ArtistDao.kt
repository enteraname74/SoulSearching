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

    @Upsert
    suspend fun upsertAll(roomArtists: List<RoomArtist>)

    @Delete
    suspend fun delete(roomArtist: RoomArtist)

    @Query("DELETE FROM RoomArtist WHERE artistId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query("DELETE FROM RoomArtist WHERE dataMode = :dataMode")
    suspend fun deleteAll(dataMode: String)

    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getFromId(artistId: UUID): Flow<RoomArtist?>

    @Query("SELECT * FROM RoomArtist WHERE dataMode = :dataMode ORDER BY artistName ASC")
    fun getAll(dataMode: String): Flow<List<RoomArtist>>

    @Query("SELECT * FROM RoomArtist WHERE artistId IN (:artistsIds)")
    suspend fun getAll(artistsIds: List<UUID>): List<RoomArtist>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE dataMode = :dataMode ORDER BY artistName ASC")
    fun getAllArtistWithMusics(dataMode: String): Flow<List<RoomArtistWithMusics?>>

    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName LIMIT 1")
    suspend fun getFromName(artistName: String): RoomArtist?

    @Query("SELECT * FROM RoomArtist WHERE artistName in (:artistsNames)")
    suspend fun getAllFromName(artistsNames: List<String>): List<RoomArtist>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId: UUID): Flow<RoomArtistWithMusics?>

    @Query("SELECT * FROM RoomArtist INNER JOIN RoomMusicArtist WHERE RoomArtist.artistId = RoomMusicArtist.artistId AND RoomMusicArtist.musicId = :musicId")
    fun getArtistsOfMusic(musicId: UUID): Flow<List<RoomArtist>>
}