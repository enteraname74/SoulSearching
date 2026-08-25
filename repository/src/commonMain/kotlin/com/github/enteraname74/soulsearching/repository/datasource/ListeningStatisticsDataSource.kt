package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.LocalMonthYear
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.domain.model.statistics.PeriodStatistics
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface ListeningStatisticsDataSource {
    suspend fun upsert(listeningStatistics: ListeningStatistics)

    suspend fun getMusicStatistics(
        musicId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.MusicStats?

    suspend fun getAlbumStatistics(
        albumId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.AlbumStats?

    suspend fun getArtistStatistics(
        artistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.ArtistStats?

    suspend fun getPlaylistStatistics(
        playlistId: Uuid,
        localMonthYear: LocalMonthYear,
    ): ListeningStatistics.PlaylistStats?

    fun getPeriodStatistics(period: Period): PeriodStatistics

    fun observeAllMonthPeriods(): Flow<List<Period.Month>>

    fun observeAllYearPeriods(): Flow<List<Period.Year>>

}