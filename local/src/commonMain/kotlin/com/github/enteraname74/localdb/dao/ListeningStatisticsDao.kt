package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.soulsearching.domain.model.statistics.LightListeningStatistics
import com.github.enteraname74.localdb.model.listeningstatistics.RoomCompleteListeningStatistics
import com.github.enteraname74.localdb.model.listeningstatistics.RoomListeningStatistics
import com.github.enteraname74.localdb.model.listeningstatistics.RoomLocalMonthYear
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Dao
abstract class ListeningStatisticsDao {
    @Upsert
    abstract suspend fun upsert(listeningStatistics: RoomListeningStatistics)

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
    abstract suspend fun getMusicStatistics(
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
    abstract suspend fun getAlbumStatistics(
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
    abstract suspend fun getArtistStatistics(
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
    abstract suspend fun getPlaylistStatistics(
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
    abstract fun observeMostListenedMusicsOnPeriod(
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
    abstract fun observeMostListenedMusics(): PagingSource<Int, RoomCompleteListeningStatistics>

    @Query(
        """
            SELECT COALESCE(SUM(timeListened), 0)
            FROM RoomListeningStatistics
        """
    )
    abstract fun observeListeningTime(): Flow<Duration>

    @Query(
        """
            SELECT COALESCE(SUM(timeListened), 0)
            FROM RoomListeningStatistics 
            WHERE month IN (:months) 
            AND year IN (:years) 
        """
    )
    abstract fun observeListeningTimeOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): Flow<Duration>

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
    abstract fun observeMostPlayedMusicsOnPeriod(
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
    abstract fun observeMostPlayedAlbumsOnPeriod(
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
    abstract fun observeMostPlayedArtistsOnPeriod(
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
    abstract fun observeMostPlayedPlaylistsOnPeriod(
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
    abstract fun observeAllLocalYearMonth(): Flow<List<RoomLocalMonthYear>>

    @Query(
        """
            SELECT DISTINCT year FROM RoomListeningStatistics 
            ORDER BY year ASC
        """
    )
    abstract fun observeAllYears(): Flow<List<Int>>

    @Transaction
    @Query(
        """
            SELECT s.* FROM RoomListeningStatistics s 
            CROSS JOIN RoomCloudPreferences cp
            WHERE cp.lastStatisticsSyncMillis IS NULL
               OR s.lastUpdatedMillis > cp.lastStatisticsSyncMillis 
        """
    )
    abstract suspend fun getAllToSendToCloud(): List<RoomCompleteListeningStatistics>

    @Query(
        """
    INSERT INTO RoomListeningStatistics (
        id,
        lastUpdatedMillis,
        nbPlayed,
        timeListened,
        month,
        year,
        musicId,
        playlistId,
        albumId,
        artistId
    )
    VALUES (
        :id,
        :lastUpdatedMillis,
        :nbPlayed,
        :timeListened,
        :month,
        :year,
        :musicId,
        :playlistId,
        :albumId,
        :artistId
    )
    ON CONFLICT(id) DO UPDATE SET
        lastUpdatedMillis = MAX(
            lastUpdatedMillis,
            excluded.lastUpdatedMillis
        ),
        nbPlayed = MAX(
            nbPlayed,
            excluded.nbPlayed
        ),
        timeListened = CASE
            WHEN timeListened IS NULL
                THEN excluded.timeListened
            WHEN excluded.timeListened IS NULL
                THEN timeListened
            ELSE MAX(timeListened, excluded.timeListened)
        END
    """
    )
    protected abstract suspend fun upsertLightListeningStatistics(
        id: String,
        lastUpdatedMillis: Long,
        nbPlayed: Int,
        timeListened: Duration?,
        month: Int,
        year: Int,
        musicId: Uuid?,
        playlistId: Uuid?,
        albumId: Uuid?,
        artistId: Uuid?,
    )

    private suspend fun upsertLight(
        listeningStatistics: LightListeningStatistics
    ) {
        with(listeningStatistics) {
            upsertLightListeningStatistics(
                id = id,
                lastUpdatedMillis = lastUpdatedMillis,
                nbPlayed = nbPlayed,
                timeListened = timeListened,
                month = localMonthYear.month,
                year = localMonthYear.year,
                musicId = musicId,
                playlistId = playlistId,
                albumId = albumId,
                artistId = artistId,
            )
        }
    }

    @Transaction
    open suspend fun upsertAllLightListeningStatistics(
        statistics: List<LightListeningStatistics>,
    ) {
        statistics.forEach {
            upsertLight(it)
        }
    }

    @Query(
        """
            SELECT lastUpdatedMillis FROM RoomListeningStatistics 
            ORDER BY lastUpdatedMillis 
            LIMIT 1
        """
    )
    abstract suspend fun getLastSyncMillis(): Long?
}