package com.github.enteraname74.soulsearching.feature.settings.statistics

import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodGeneralUiStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodType

data class SettingsStatisticsState(
    val selectedPeriod: Period,
    val availablePeriods: List<Period>,
    val focusedListStatistics: ListStatistics.Type,
    val onListStatisticsFocused: (ListStatistics.Type) -> Unit,
    val periodGeneralUiStatistics: PeriodGeneralUiStatistics?,
    val listStatistics: List<ListStatistics>,
    val onNewPeriod: (Period) -> Unit,
    val onNewPeriodType: (PeriodType) -> Unit,
    val navigateBack: () -> Unit,
)