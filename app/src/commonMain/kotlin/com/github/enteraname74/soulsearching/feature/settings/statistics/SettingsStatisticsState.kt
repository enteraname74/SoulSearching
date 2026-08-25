package com.github.enteraname74.soulsearching.feature.settings.statistics

import com.github.enteraname74.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodType
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics

data class SettingsStatisticsState(
    val selectedPeriod: Period,
    val availablePeriods: List<Period>,
    val currentStatistics: PeriodUiStatistics,
    val onNewPeriod: (Period) -> Unit,
    val onNewPeriodType: (PeriodType) -> Unit,
    val navigateBack: () -> Unit,
)