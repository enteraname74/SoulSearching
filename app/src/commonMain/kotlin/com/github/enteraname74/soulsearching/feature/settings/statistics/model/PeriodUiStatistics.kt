package com.github.enteraname74.soulsearching.feature.settings.statistics.model

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodStatistics
import kotlinx.coroutines.flow.Flow

data class PeriodUiStatistics(
    val period: Period,
    val stats: List<Stats>,
) {
    data class Stats(
        val title: SoulTextSpec,
        val optionalContent: OptionalContent? = null,
        val data: Flow<PagingData<StatisticsUiElement>>,
    ) {
        data class OptionalContent(
            val title: SoulTextSpec,
            val flow: Flow<SoulTextSpec>,
        )
    }
}