package com.github.enteraname74.soulsearching.domain.usecase.listeningstatistics

import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodStatistics
import com.github.enteraname74.soulsearching.domain.repository.ListeningStatisticsRepository

class GetPeriodStatisticsUseCase(
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    fun invoke(period: Period): PeriodStatistics =
        listeningStatisticsRepository.getPeriodStatistics(period)
}