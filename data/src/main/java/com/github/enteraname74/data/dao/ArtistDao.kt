package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.Artist
import com.github.enteraname74.data.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface ArtistDao {

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

    @Query("SELECT * FROM Artist WHERE artistName = :artistName LIMIT 1")
    fun getArtistFromInfo(artistName: String): Artist?

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics>

    @Transaction
    @Query("SELECT * FROM Artist WHERE isInQuickAccess = 1")
    fun getAllArtistsFromQuickAccess(): Flow<List<ArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Artist WHERE artistId = :artistId")
    fun getArtistWithMusicsSimple(artistId: UUID): ArtistWithMusics

    @Query("UPDATE Artist SET coverId = :newCoverId WHERE artistId = :artistId")
    fun updateArtistCover(newCoverId : UUID, artistId : UUID)

    @Query("SELECT COUNT(*) FROM Artist WHERE coverId = :coverId")
    fun getNumberOfArtistsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE Artist SET isInQuickAccess = :newQuickAccessState WHERE artistId = :artistId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID)

    @Query("SELECT nbPlayed FROM Artist WHERE artistId = :artistId")
    fun getNbPlayedOfArtist(artistId: UUID): Int

    @Query("UPDATE Artist SET nbPlayed = :newNbPlayed WHERE artistId = :artistId")
    fun updateNbPlayed(newNbPlayed: Int, artistId: UUID)
}