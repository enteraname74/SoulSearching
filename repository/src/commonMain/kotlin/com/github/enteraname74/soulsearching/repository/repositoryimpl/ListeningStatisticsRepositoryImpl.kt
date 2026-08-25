package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.LocalMonthYear
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.domain.model.statistics.PeriodStatistics
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository
import com.github.enteraname74.soulsearching.repository.datasource.ListeningStatisticsDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class ListeningStatisticsRepositoryImpl(
    private val dataSource: ListeningStatisticsDataSource,
) : ListeningStatisticsRepository {
    override suspend fun upsert(listeningStatistics: ListeningStatistics) {
        dataSource.upsert(listeningStatistics)
    }

    override suspend fun getMusicStatistics(
        musicId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.MusicStats? =
        dataSource.getMusicStatistics(
            musicId = musicId,
            localMonthYear = localMonthYear,
        )

    override suspend fun getAlbumStatistics(
        albumId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.AlbumStats? =
        dataSource.getAlbumStatistics(
            albumId = albumId,
            localMonthYear = localMonthYear,
        )

    override suspend fun getArtistStatistics(
        artistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.ArtistStats? =
        dataSource.getArtistStatistics(
            artistId = artistId,
            localMonthYear = localMonthYear,
        )

    override suspend fun getPlaylistStatistics(
        playlistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.PlaylistStats? =
        dataSource.getPlaylistStatistics(
            playlistId = playlistId,
            localMonthYear = localMonthYear,
        )

    override fun getPeriodStatistics(period: Period): PeriodStatistics =
        dataSource.getPeriodStatistics(period)

    override fun observeAllMonthPeriods(): Flow<List<Period.Month>> =
        dataSource.observeAllMonthPeriods()

    override fun observeAllYearPeriods(): Flow<List<Period.Year>> =
        dataSource.observeAllYearPeriods()
}