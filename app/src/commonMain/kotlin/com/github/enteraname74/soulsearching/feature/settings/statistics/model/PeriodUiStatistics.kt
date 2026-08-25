package com.github.enteraname74.soulsearching.feature.settings.statistics.model

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.domain.model.statistics.PeriodStatistics
import kotlinx.coroutines.flow.Flow

data class PeriodUiStatistics(
    val period: Period,
    val stats: List<Stats>,
) {
    data class Stats(
        val title: String,
        val data: Flow<PagingData<StatisticsUiElement>>,
    )
}