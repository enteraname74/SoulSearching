package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.ext.months
import com.github.enteraname74.localdb.ext.toPagingData
import com.github.enteraname74.localdb.ext.years
import com.github.enteraname74.localdb.model.listeningstatistics.toRoomListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.LocalMonthYear
import com.github.enteraname74.soulsearching.domain.model.statistics.LightListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.localdb.model.listeningstatistics.RoomPeriodTotals
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodGeneralStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodPagedStatistics
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import com.github.enteraname74.soulsearching.repository.datasource.listeningstatistics.ListeningStatisticsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class RoomListeningStatisticsLocalDataSourceImpl(
    private val appDatabase: AppDatabase,
) : ListeningStatisticsLocalDataSource {
    override suspend fun upsert(listeningStatistics: ListeningStatistics) {
        appDatabase.listeningStatisticsDao.upsert(
            listeningStatistics.toRoomListeningStatistics()
        )
    }

    override suspend fun getMusicStatistics(
        musicId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.MusicStats? =
        appDatabase.listeningStatisticsDao.getMusicStatistics(
            musicId = musicId,
            year = localMonthYear.year,
            month = localMonthYear.month,
        )?.toMusicStats()

    override suspend fun getAlbumStatistics(
        albumId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.AlbumStats? =
        appDatabase.listeningStatisticsDao.getAlbumStatistics(
            albumId = albumId,
            year = localMonthYear.year,
            month = localMonthYear.month,
        )?.toAlbumStats()

    override suspend fun getArtistStatistics(
        artistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.ArtistStats? =
        appDatabase.listeningStatisticsDao.getArtistStatistics(
            artistId = artistId,
            year = localMonthYear.year,
            month = localMonthYear.month,
        )?.toArtistStats()

    override suspend fun getPlaylistStatistics(
        playlistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.PlaylistStats? =
        appDatabase.listeningStatisticsDao.getPlaylistStatistics(
            playlistId = playlistId,
            year = localMonthYear.year,
            month = localMonthYear.month,
        )?.toPlaylistStats()

    override suspend fun getCollectionStatistics(
        collectionId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.CollectionStats? =
        appDatabase.listeningStatisticsDao.getCollectionStatistics(
            collectionId = collectionId,
            year = localMonthYear.year,
            month = localMonthYear.month,
        )?.toCollectionStats()

    override fun getPeriodStatistics(period: Period): PeriodPagedStatistics =
        when (period) {
            Period.All -> getAllPeriodStatistics()
            is Period.Specific -> getSpecificPeriodStatistics(period)
        }

    private fun getAllPeriodStatistics(): PeriodPagedStatistics =
        PeriodPagedStatistics.All(
            period = Period.All,
            mostListenedMusics = { appDatabase.listeningStatisticsDao.observeMostListenedMusics() }.toPagingData { it.toMusicStats() },
            mostPlayedMusics = { appDatabase.musicDao.getMostPlayed() }.toPagingData { it.toMusicStats() },
            mostPlayedArtists = { appDatabase.artistDao.getMostListened() }.toPagingData { it.toArtistStats() },
            mostPlayedAlbums = { appDatabase.albumDao.getMostListened() }.toPagingData { it.toAlbumStats() },
            mostPlayedPlaylists = { appDatabase.playlistDao.getMostListened() }.toPagingData { it.toPlaylistStats() },
            mostPlayedCollections = { appDatabase.collectionDao.getMostListened() }.toPagingData { it.toCollectionStats() },
            artistsWithMostMusics = { appDatabase.artistDao.getArtistsWithMostMusics() }.toPagingData { it.toArtistStats() },
        )

    private fun getSpecificPeriodStatistics(period: Period.Specific): PeriodPagedStatistics {
        val months = period.months()
        val years = period.years()

        return PeriodPagedStatistics.Specific(
            period = period,
            mostListenedMusics = {
                appDatabase.listeningStatisticsDao.observeMostListenedMusicsOnPeriod(
                    months = months,
                    years = years,
                )
            }.toPagingData { it.toMusicStats() },
            mostPlayedMusics = {
                appDatabase.listeningStatisticsDao.observeMostPlayedMusicsOnPeriod(
                    months = months,
                    years = years,
                )
            }.toPagingData { it.toMusicStats() },
            mostPlayedArtists = {
                appDatabase.listeningStatisticsDao.observeMostPlayedArtistsOnPeriod(
                    months = months,
                    years = years,
                )
            }.toPagingData { it.toArtistStats() },
            mostPlayedAlbums = {
                appDatabase.listeningStatisticsDao.observeMostPlayedAlbumsOnPeriod(
                    months = months,
                    years = years,
                )
            }.toPagingData { it.toAlbumStats() },
            mostPlayedPlaylists = {
                appDatabase.listeningStatisticsDao.observeMostPlayedPlaylistsOnPeriod(
                    months = months,
                    years = years,
                )
            }.toPagingData { it.toPlaylistStats() },
            mostPlayedCollections = {
                appDatabase.listeningStatisticsDao.observeMostPlayedCollectionsOnPeriod(
                    months = months,
                    years = years,
                )
            }.toPagingData { it.toCollectionStats() },
        )
    }

    override fun observePeriodGeneralStatistics(period: Period): Flow<PeriodGeneralStatistics> =
        when (period) {
            Period.All -> combine(
                appDatabase.listeningStatisticsDao.observeMostPlayedMusic(),
                appDatabase.listeningStatisticsDao.observeMostListenedMusic(),
                appDatabase.listeningStatisticsDao.observeTotals(),
            ) { mostPlayedMusic, mostListenedMusic, totals ->
                toPeriodGeneralStatistics(
                    mostPlayedMusic = mostPlayedMusic?.toMusicStats(),
                    mostListenedMusic = mostListenedMusic?.toMusicStats(),
                    totals = totals,
                )
            }

            is Period.Specific -> {
                val months = period.months()
                val years = period.years()

                combine(
                    appDatabase.listeningStatisticsDao.observeMostPlayedMusicOnPeriod(
                        months = months,
                        years = years,
                    ),
                    appDatabase.listeningStatisticsDao.observeMostListenedMusicOnPeriod(
                        months = months,
                        years = years,
                    ),
                    appDatabase.listeningStatisticsDao.observeTotalsOnPeriod(
                        months = months,
                        years = years,
                    ),
                ) { mostPlayedMusic, mostListenedMusic, totals ->
                    toPeriodGeneralStatistics(
                        mostPlayedMusic = mostPlayedMusic?.toMusicStats(),
                        mostListenedMusic = mostListenedMusic?.toMusicStats(),
                        totals = totals,
                    )
                }
            }
        }

    private fun toPeriodGeneralStatistics(
        mostPlayedMusic: ListeningStatistics.MusicStats?,
        mostListenedMusic: ListeningStatistics.MusicStats?,
        totals: RoomPeriodTotals,
    ): PeriodGeneralStatistics =
        PeriodGeneralStatistics(
            mostPlayedMusic = mostPlayedMusic,
            mostListenedMusic = mostListenedMusic,
            totalMusicsPlayed = totals.totalMusicsPlayed,
            totalUniqueMusicsPlayed = totals.totalUniqueMusicsPlayed,
            totalUniqueArtistsPlayed = totals.totalUniqueArtistsPlayed,
            totalListeningTime = totals.totalListeningTime,
        )

    override fun observeAllMonthPeriods(): Flow<List<Period.Month>> =
        appDatabase.listeningStatisticsDao.observeAllLocalYearMonth().map { list ->
            val mappedList = list.map {
                Period.Month(
                    month = it.month,
                    year = it.year,
                )
            }
            // We add the current period, for UI purpose.
            val today = DateUtils.currentMonthYear().let { monthYear ->
                Period.Month(
                    month = monthYear.month,
                    year = monthYear.year,
                )
            }
            (mappedList + today).distinct()
        }

    override fun observeAllYearPeriods(): Flow<List<Period.Year>> =
        appDatabase.listeningStatisticsDao.observeAllYears().map { list ->
            val mappedList = list.map { Period.Year(it) }

            val today = DateUtils.currentMonthYear().let { monthYear ->
                Period.Year(
                    year = monthYear.year,
                )
            }

            (mappedList + today).distinct()
        }

    override suspend fun getAllToSendToCloud(): List<ListeningStatistics> =
        appDatabase.listeningStatisticsDao.getAllToSendToCloud().mapNotNull { it.toListeningStatistics() }

    override suspend fun upsertLightListeningStatistics(listeningStatistics: List<LightListeningStatistics>) {
        appDatabase.listeningStatisticsDao.upsertAllLightListeningStatistics(listeningStatistics)
    }

    override suspend fun getLastSyncMillis(): Long? =
        appDatabase.listeningStatisticsDao.getLastSyncMillis()
}
