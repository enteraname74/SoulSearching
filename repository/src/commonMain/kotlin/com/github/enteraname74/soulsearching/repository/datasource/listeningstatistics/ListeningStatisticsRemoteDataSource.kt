package com.github.enteraname74.soulsearching.repository.datasource.listeningstatistics

import com.github.enteraname74.soulsearching.domain.model.statistics.CloudListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics

interface ListeningStatisticsRemoteDataSource {
    suspend fun upsertAll(
        statistics: List<ListeningStatistics>,
    )

    suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?,
    ): List<CloudListeningStatistics>
}