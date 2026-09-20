package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics
import kotlinx.coroutines.launch

@Composable
internal fun SettingsStatisticsPeriodUiStatisticsView(
    currentStatistics: PeriodUiStatistics,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { currentStatistics.stats.size }
    )

    Column {
        SettingsStatisticsSectionIndicatorList(
            selectedIndex = pagerState.currentPage,
            listSize = pagerState.pageCount,
            onClick = { selectedIndex ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = selectedIndex,
                    )
                }
            }
        )
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth(),
            state = pagerState,
        ) { index ->
            SettingsStatisticsSection(
                stats = currentStatistics.stats[index],
            )
        }
    }
}