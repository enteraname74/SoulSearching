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
    suspend fun insertArtist(roomArtist: RoomArtist)

    @Delete
    suspend fun deleteArtist(roomArtist: RoomArtist)

    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    suspend fun getArtistFromId(artistId: UUID): RoomArtist?

    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAllArtistsSortByNameAsFlow(): Flow<List<RoomArtist>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY artistName DESC")
    fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY addedDate ASC")
    fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY addedDate DESC")
    fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY nbPlayed ASC")
    fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY nbPlayed DESC")
    fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName AND artistId != :artistId")
    suspend fun getPossibleDuplicatedArtistName(
        artistId: UUID,
        artistName: String
    ): RoomArtistWithMusics?

    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName LIMIT 1")
    suspend fun getArtistFromInfo(artistName: String): RoomArtist?

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<RoomArtistWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE isInQuickAccess = 1")
    fun getAllArtistsFromQuickAccessAsFlow(): Flow<List<RoomArtistWithMusics?>>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    suspend fun getArtistWithMusics(artistId: UUID): RoomArtistWithMusics?

    @Query("UPDATE RoomArtist SET coverId = :newCoverId WHERE artistId = :artistId")
    suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID)

    @Query("SELECT COUNT(*) FROM RoomArtist WHERE coverId = :coverId")
    suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int

    @Query("UPDATE RoomArtist SET isInQuickAccess = :newQuickAccessState WHERE artistId = :artistId")
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID)

    @Query("SELECT nbPlayed FROM RoomArtist WHERE artistId = :artistId")
    suspend fun getNbPlayedOfArtist(artistId: UUID): Int?

    @Query("UPDATE RoomArtist SET nbPlayed = :newNbPlayed WHERE artistId = :artistId")
    suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID)
}