package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Upsert
    suspend fun insertArtist(artist : Artist)

    @Delete
    suspend fun deleteArtist(artist : Artist)

    @Query("SELECT * FROM Artist ORDER BY name ASC")
    fun getAllArtists(): Flow<List<Artist>>
}