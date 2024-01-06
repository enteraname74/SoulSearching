package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomArtist
import com.github.enteraname74.data.model.RoomArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface ArtistDao {

    @Upsert
    suspend fun insertArtist(roomArtist: RoomArtist)

    @Delete
    suspend fun deleteArtist(roomArtist: RoomArtist)

    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistFromId(artistId: UUID) : RoomArtist?

    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAllArtistsSortByName(): Flow<List<RoomArtist>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAllArtistsWithMusicsSortByNameAsc(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY artistName DESC")
    fun getAllArtistWithMusicsSortByNameDesc(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY addedDate ASC")
    fun getAllArtistWithMusicsSortByAddedDateAsc(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY addedDate DESC")
    fun getAllArtistWithMusicsSortByAddedDateDesc(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY nbPlayed ASC")
    fun getAllArtistWithMusicsSortByNbPlayedAsc(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY nbPlayed DESC")
    fun getAllArtistWithMusicsSortByNbPlayedDesc(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName AND artistId != :artistId")
    fun getPossibleDuplicatedArtistName(artistId: UUID, artistName: String) : RoomArtistWithMusics?

    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName LIMIT 1")
    fun getArtistFromInfo(artistName: String): RoomArtist?

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId: UUID): Flow<RoomArtistWithMusics>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE isInQuickAccess = 1")
    fun getAllArtistsFromQuickAccess(): Flow<List<RoomArtistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistWithMusicsSimple(artistId: UUID): RoomArtistWithMusics

    @Query("UPDATE RoomArtist SET coverId = :newCoverId WHERE artistId = :artistId")
    fun updateArtistCover(newCoverId : UUID, artistId : UUID)

    @Query("SELECT COUNT(*) FROM RoomArtist WHERE coverId = :coverId")
    fun getNumberOfArtistsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE RoomArtist SET isInQuickAccess = :newQuickAccessState WHERE artistId = :artistId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID)

    @Query("SELECT nbPlayed FROM RoomArtist WHERE artistId = :artistId")
    fun getNbPlayedOfArtist(artistId: UUID): Int

    @Query("UPDATE RoomArtist SET nbPlayed = :newNbPlayed WHERE artistId = :artistId")
    fun updateNbPlayed(newNbPlayed: Int, artistId: UUID)
}