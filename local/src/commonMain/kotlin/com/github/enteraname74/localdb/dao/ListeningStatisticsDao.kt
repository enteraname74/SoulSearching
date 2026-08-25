package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.listeningstatistics.RoomCompleteListeningStatistics
import com.github.enteraname74.localdb.model.listeningstatistics.RoomListeningStatistics
import com.github.enteraname74.localdb.model.listeningstatistics.RoomLocalMonthYear
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface ListeningStatisticsDao {
    @Upsert
    suspend fun upsert(listeningStatistics: RoomListeningStatistics)

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE musicId = :musicId 
            AND month = :month 
            AND year = :year 
            LIMIT 1
        """
    )
    suspend fun getMusicStatistics(
        musicId: Uuid,
        month: Int,
        year: Int,
    ): RoomCompleteListeningStatistics?

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE albumId = :albumId 
            AND month = :month 
            AND year = :year 
            LIMIT 1
        """
    )
    suspend fun getAlbumStatistics(
        albumId: Uuid,
        month: Int,
        year: Int,
    ): RoomCompleteListeningStatistics?

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE artistId = :artistId 
            AND month = :month 
            AND year = :year 
            LIMIT 1
        """
    )
    suspend fun getArtistStatistics(
        artistId: Uuid,
        month: Int,
        year: Int,
    ): RoomCompleteListeningStatistics?

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE playlistId = :playlistId 
            AND month = :month 
            AND year = :year 
            LIMIT 1
        """
    )
    suspend fun getPlaylistStatistics(
        playlistId: Uuid,
        month: Int,
        year: Int,
    ): RoomCompleteListeningStatistics?

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            AND timeListened IS NOT NULL 
            AND timeListened > 0 
            ORDER BY timeListened DESC
        """
    )
    fun observeMostListenedMusicsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND timeListened IS NOT NULL 
            AND timeListened > 0 
            ORDER BY timeListened DESC
        """
    )
    fun observeMostListenedMusics(): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            ORDER BY nbPlayed DESC
        """
    )
    fun observeMostPlayedMusicsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE albumId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            ORDER BY nbPlayed DESC
        """
    )
    fun observeMostPlayedAlbumsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE artistId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            ORDER BY nbPlayed DESC
        """
    )
    fun observeMostPlayedArtistsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomListeningStatistics 
            WHERE playlistId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            ORDER BY nbPlayed DESC
        """
    )
    fun observeMostPlayedPlaylistsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT DISTINCT month, year FROM RoomListeningStatistics 
            ORDER BY year ASC, month ASC
        """
    )
    fun observeAllLocalYearMonth(): Flow<List<RoomLocalMonthYear>>

    @Query(
        """
            SELECT DISTINCT year FROM RoomListeningStatistics 
            ORDER BY year ASC
        """
    )
    fun observeAllYears(): Flow<List<Int>>
}