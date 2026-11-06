package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.settings.statistics.SettingsStatisticsState
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.firstLargeCards
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.smallCards
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics

// TODO STATS: empty view
@Composable
fun SettingsStatisticsView(
    state: SettingsStatisticsState,
) {
    val windowSize = rememberWindowSize()

    when (windowSize) {
        WindowSize.Small -> {
            SmallScreen(
                state = state,
            )
        }

        else -> {
            LargeScreen(
                state = state,
            )
        }
    }
}

@Composable
private fun SmallScreen(
    state: SettingsStatisticsState,
) {
    val shownList = state.listStatistics.find { it.type == state.focusedListStatistics }?.list?.collectAsLazyPagingItems()

    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = UiConstants.Spacing.medium,
        )
    ) {
        with(SettingsStatisticsFactory) {
            smallCards(state.periodGeneralUiStatistics)
            musicStatisticsCards(
                generalUiStatistics = state.periodGeneralUiStatistics,
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.mediumPlus,
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.small,
                    ),
            )
            listStatisticsHeader(
                statistics = state.listStatistics,
                selectedType = state.focusedListStatistics,
                onTypeChange = state.onListStatisticsFocused,
            )
            shownList?.let {
                listStatisticsItems(
                    list = it,
                )
            }
            playerSpacer()
        }
    }
}

@Composable
private fun LargeScreen(
    state: SettingsStatisticsState,
) {
    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = UiConstants.Spacing.medium,
        )
    ) {
        smallCards(state.periodGeneralUiStatistics)
        firstLargeCards(
            generalUiStatistics = state.periodGeneralUiStatistics,
            artistStatistics = state.listStatistics.find { it.type == ListStatistics.Type.Artists } as? ListStatistics.Artists,
        )
    }
}
