package com.github.enteraname74.soulsearching.feature.settings.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsPeriodUiStatisticsView
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsPeriodsView

@Composable
fun SettingsStatisticsScreen(
    state: SettingsStatisticsState,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = strings.statisticsTitle,
                leftAction = TopBarNavigationAction(
                    onClick = state.navigateBack,
                )
            )
            SettingsStatisticsPeriodsView(
                currentPeriod = state.selectedPeriod,
                allPeriods = state.availablePeriods,
                onPeriodChange = state.onNewPeriod,
                onPeriodTypeChange = state.onNewPeriodType,
            )
            if (state.currentStatistics.stats.isNotEmpty()) {
                SettingsStatisticsPeriodUiStatisticsView(
                    currentStatistics = state.currentStatistics,
                )
            }
        }
    }
}
