package com.github.enteraname74.soulsearching.feature.settings.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsPeriodUiStatisticsView
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsPeriodsView
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsStatisticsScreen(
    state: SettingsStatisticsState,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = CoreRes.string.statistics_title.toTextSpec(),
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
