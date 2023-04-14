package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ArtistDao {

    @Upsert
    suspend fun insertArtist(artist : Artist)

    @Delete
    suspend fun deleteArtist(artist : Artist)

    @Query("SELECT * FROM Artist ORDER BY artistName ASC")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("SELECT * FROM Artist WHERE artistName = :artistName")
    fun getArtistFromInfo(artistName : String) : Artist?

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId : UUID): Flow<ArtistWithMusics>

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistWithMusicsSimple(artistId : UUID): ArtistWithMusics
}