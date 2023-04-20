package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ArtistDao {

    @Upsert
    suspend fun insertArtist(artist: Artist)

    @Delete
    suspend fun deleteArtist(artist: Artist)

    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistFromId(artistId: UUID) : Artist?

    @Query("SELECT * FROM Artist ORDER BY artistName ASC")
    fun getAllArtistsSortByName(): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * FROM Artist ORDER BY artistName ASC")
    fun getAllArtistsWithMusicsSortByNameAsc(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist ORDER BY artistName DESC")
    fun getAllArtistWithMusicsSortByNameDesc(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist ORDER BY addedDate ASC")
    fun getAllArtistWithMusicsSortByAddedDateAsc(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist ORDER BY addedDate DESC")
    fun getAllArtistWithMusicsSortByAddedDateDesc(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist ORDER BY nbPlayed ASC")
    fun getAllArtistWithMusicsSortByNbPlayedAsc(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist ORDER BY nbPlayed DESC")
    fun getAllArtistWithMusicsSortByNbPlayedDesc(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistName = :artistName AND artistId != :artistId")
    fun getPossibleDuplicatedArtistName(artistId: UUID, artistName: String) : ArtistWithMusics?

    @Query("SELECT * FROM Artist WHERE artistName = :artistName")
    fun getArtistFromInfo(artistName: String): Artist?

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics>

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistWithMusicsSimple(artistId: UUID): ArtistWithMusics
}