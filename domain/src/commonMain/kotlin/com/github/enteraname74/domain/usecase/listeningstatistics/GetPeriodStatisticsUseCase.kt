package com.github.enteraname74.domain.usecase.listeningstatistics

import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.domain.model.statistics.PeriodStatistics
import com.github.enteraname74.domain.repository.ListeningStatisticsRepository

class GetPeriodStatisticsUseCase(
    private val listeningStatisticsRepository: ListeningStatisticsRepository,
) {
    fun invoke(period: Period): PeriodStatistics =
        listeningStatisticsRepository.getPeriodStatistics(period)
}