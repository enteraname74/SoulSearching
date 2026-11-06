package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.firstMediumCards
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.otherCards
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.playerSpacer
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.smallCards
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.SettingsStatisticsFactory.spacer
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsListStatisticsCard
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics

// TODO STATS: empty view
// TODO STATS: click on more to see dialog style list
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
        WindowSize.Medium -> {
            MediumScreen(
                state = state,
            )
        }
        WindowSize.Large -> {
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
            spacer(UiConstants.Spacing.mediumPlus)
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
            musicStatistics = state.listStatistics.find { it.type == ListStatistics.Type.Musics } as? ListStatistics.Musics,
        )
        otherCards(
            listStatistics = state.listStatistics.filter { it.type != ListStatistics.Type.Musics },
            itemsPerRow = 3,
        )
        spacer(UiConstants.Spacing.large)
        playerSpacer()
    }
}

@Composable
private fun MediumScreen(
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
        firstMediumCards(
            generalUiStatistics = state.periodGeneralUiStatistics,
        )
        item {
            state.listStatistics.find { it.type == ListStatistics.Type.Musics }?.let {
                SettingsStatisticsListStatisticsCard(
                    modifier = Modifier
                        .padding(
                            start = UiConstants.Spacing.large,
                            end = UiConstants.Spacing.large,
                            bottom = UiConstants.Spacing.large,
                        )
                        .fillMaxWidth(),
                    listStatistics = it,
                    maxItems = 5,
                )
            }
        }
        otherCards(
            listStatistics = state.listStatistics.filter { it.type != ListStatistics.Type.Musics },
            itemsPerRow = 2,
        )
        spacer(UiConstants.Spacing.large)
        playerSpacer()
    }
}
