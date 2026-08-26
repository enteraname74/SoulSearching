package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.LocalMonthYear
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.domain.model.statistics.PeriodStatistics
import com.github.enteraname74.domain.util.DateUtils
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.ext.months
import com.github.enteraname74.localdb.ext.toPagingData
import com.github.enteraname74.localdb.ext.years
import com.github.enteraname74.localdb.model.listeningstatistics.toRoomListeningStatistics
import com.github.enteraname74.soulsearching.repository.datasource.ListeningStatisticsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.uuid.Uuid

class RoomListeningStatisticsDataSourceImpl(
    private val appDatabase: AppDatabase,
) : ListeningStatisticsDataSource {
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

    override fun getPeriodStatistics(period: Period): PeriodStatistics =
        when (period) {
            Period.All -> getAllPeriodStatistics()
            is Period.Specific -> getSpecificPeriodStatistics(period)
        }

    private fun getAllPeriodStatistics(): PeriodStatistics =
        PeriodStatistics.All(
            period = Period.All,
            mostListenedMusics = { appDatabase.listeningStatisticsDao.observeMostListenedMusics() }.toPagingData { it.toMusicStats() },
            mostPlayedMusics = { appDatabase.musicDao.getMostPlayed() }.toPagingData { it.toMusicStats() },
            mostPlayedArtists = { appDatabase.artistDao.getMostListened() }.toPagingData { it.toArtistStats() },
            mostPlayedAlbums = { appDatabase.albumDao.getMostListened() }.toPagingData { it.toAlbumStats() },
            mostPlayedPlaylists = { appDatabase.playlistDao.getMostListened() }.toPagingData { it.toPlaylistStats() },
            artistsWithMostMusics = { appDatabase.artistDao.getArtistsWithMostMusics() }.toPagingData { it.toArtistStats() },
            listeningTime = appDatabase.listeningStatisticsDao.observeListeningTime(),
        )

    private fun getSpecificPeriodStatistics(period: Period.Specific): PeriodStatistics {
        val months = period.months()
        val years = period.years()

        return PeriodStatistics.Specific(
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
            listeningTime = appDatabase.listeningStatisticsDao.observeListeningTimeOnPeriod(
                months = months,
                years = years,
            ),
        )
    }

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
}