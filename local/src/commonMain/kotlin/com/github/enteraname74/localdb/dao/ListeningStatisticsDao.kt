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
import com.github.enteraname74.localdb.model.listeningstatistics.RoomPeriodTotals
import com.github.enteraname74.localdb.model.RoomCompleteMusic
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
            WHERE collectionId = :collectionId
            AND month = :month
            AND year = :year
            LIMIT 1
        """
    )
    abstract suspend fun getCollectionStatistics(
        collectionId: Uuid,
        month: Int,
        year: Int,
    ): RoomCompleteListeningStatistics?

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY musicId 
            HAVING SUM(timeListened) > 0 
            ORDER BY timeListened DESC, musicId
        """
    )
    abstract fun observeMostListenedMusicsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            GROUP BY musicId 
            HAVING SUM(timeListened) > 0 
            ORDER BY timeListened DESC, musicId
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

    /**
     * Returns the music with the most plays on the period, its statistics summed over the period.
     */
    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY musicId 
            ORDER BY nbPlayed DESC, musicId 
            LIMIT 1
        """
    )
    abstract fun observeMostPlayedMusicOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): Flow<RoomCompleteListeningStatistics?>

    /**
     * Returns the music with the most time listened on the period, its statistics summed over the period.
     */
    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY musicId 
            HAVING SUM(timeListened) > 0 
            ORDER BY timeListened DESC, musicId 
            LIMIT 1
        """
    )
    abstract fun observeMostListenedMusicOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): Flow<RoomCompleteListeningStatistics?>

    /**
     * Returns the music with the most time listened, its statistics summed over every period.
     */
    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            GROUP BY musicId 
            HAVING SUM(timeListened) > 0 
            ORDER BY timeListened DESC, musicId 
            LIMIT 1
        """
    )
    abstract fun observeMostListenedMusic(): Flow<RoomCompleteListeningStatistics?>

    /**
     * Uses the all time counter of the music, like the all time list of most played musics.
     */
    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE nbPlayed >= 1 AND isHidden = 0 
            AND scope != 'SharedPlayedList' 
            ORDER BY nbPlayed DESC, musicId 
            LIMIT 1
        """
    )
    abstract fun observeMostPlayedMusic(): Flow<RoomCompleteMusic?>

    /**
     * Unique artists are the distinct artists of the musics played on the period.
     */
    @Query(
        """
            SELECT 
            COALESCE(SUM(CASE WHEN musicId IS NOT NULL THEN nbPlayed END), 0) AS totalMusicsPlayed, 
            COUNT(DISTINCT musicId) AS totalUniqueMusicsPlayed, 
            (
                SELECT COUNT(DISTINCT musicArtist.artistId) 
                FROM RoomListeningStatistics AS musicStats 
                JOIN RoomMusicArtist AS musicArtist ON musicArtist.musicId = musicStats.musicId 
                WHERE musicStats.musicId IS NOT NULL 
                AND musicStats.month IN (:months) 
                AND musicStats.year IN (:years)
            ) AS totalUniqueArtistsPlayed, 
            COALESCE(SUM(timeListened), 0) AS totalListeningTime 
            FROM RoomListeningStatistics 
            WHERE month IN (:months) 
            AND year IN (:years)
        """
    )
    abstract fun observeTotalsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): Flow<RoomPeriodTotals>

    /**
     * Uses the all time counters of musics, like the all time lists.
     */
    @Query(
        """
            SELECT 
            (
                SELECT COALESCE(SUM(nbPlayed), 0) FROM RoomMusic 
                WHERE isHidden = 0 AND scope != 'SharedPlayedList'
            ) AS totalMusicsPlayed, 
            (
                SELECT COUNT(*) FROM RoomMusic 
                WHERE nbPlayed >= 1 AND isHidden = 0 AND scope != 'SharedPlayedList'
            ) AS totalUniqueMusicsPlayed, 
            (
                SELECT COUNT(DISTINCT musicArtist.artistId) 
                FROM RoomMusic AS music 
                JOIN RoomMusicArtist AS musicArtist ON musicArtist.musicId = music.musicId 
                WHERE music.nbPlayed >= 1 AND music.isHidden = 0 AND music.scope != 'SharedPlayedList'
            ) AS totalUniqueArtistsPlayed, 
            (
                SELECT COALESCE(SUM(timeListened), 0) FROM RoomListeningStatistics
            ) AS totalListeningTime
        """
    )
    abstract fun observeTotals(): Flow<RoomPeriodTotals>

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE musicId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY musicId 
            ORDER BY nbPlayed DESC, musicId
        """
    )
    abstract fun observeMostPlayedMusicsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, NULL AS musicId, NULL AS playlistId, albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE albumId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY albumId 
            ORDER BY nbPlayed DESC, albumId
        """
    )
    abstract fun observeMostPlayedAlbumsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, NULL AS musicId, NULL AS playlistId, NULL AS albumId, artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE artistId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY artistId 
            ORDER BY nbPlayed DESC, artistId
        """
    )
    abstract fun observeMostPlayedArtistsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, NULL AS musicId, playlistId, NULL AS albumId, NULL AS artistId, NULL AS collectionId 
            FROM RoomListeningStatistics 
            WHERE playlistId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY playlistId 
            ORDER BY nbPlayed DESC, playlistId
        """
    )
    abstract fun observeMostPlayedPlaylistsOnPeriod(
        months: List<Int>,
        years: List<Int>,
    ): PagingSource<Int, RoomCompleteListeningStatistics>

    @Transaction
    @Query(
        """
            SELECT MIN(id) AS id, MAX(lastUpdatedMillis) AS lastUpdatedMillis, 
            SUM(nbPlayed) AS nbPlayed, SUM(timeListened) AS timeListened, 
            MAX(month) AS month, MAX(year) AS year, NULL AS musicId, NULL AS playlistId, NULL AS albumId, NULL AS artistId, collectionId 
            FROM RoomListeningStatistics 
            WHERE collectionId IS NOT NULL 
            AND month IN (:months) 
            AND year IN (:years) 
            GROUP BY collectionId 
            ORDER BY nbPlayed DESC, collectionId
        """
    )
    abstract fun observeMostPlayedCollectionsOnPeriod(
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
        artistId,
        collectionId
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
        :artistId,
        :collectionId
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
        collectionId: Uuid?,
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
                collectionId = collectionId,
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
