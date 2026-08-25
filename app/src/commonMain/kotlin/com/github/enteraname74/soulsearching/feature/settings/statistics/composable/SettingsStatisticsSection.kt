package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.NoElementView
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.StatisticsUiElement

@Composable
fun SettingsStatisticsSection(
    stats: PeriodUiStatistics.Stats,
) {
    val windowSize = rememberWindowSize()
    val elements = stats.data.collectAsLazyPagingItems()

    when (windowSize) {
        WindowSize.Small -> {
            SmallScreen(
                title = stats.title,
                elements = elements,
            )
        }

        else -> {
            LargeScreen(
                title = stats.title,
                elements = elements,
            )
        }
    }
}

@Composable
private fun SmallScreen(
    title: String,
    elements: LazyPagingItems<StatisticsUiElement>,
) {
    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = UiConstants.Spacing.large),
        contentPadding = PaddingValues(
            horizontal = UiConstants.Spacing.large,
        ),
    ) {
        with(SettingsStatisticsSectionFactory) {
            title(title)
            smallList(elements = elements)
            item {
                SoulPlayerSpacer()
            }
        }
    }
}

@Composable
private fun LargeScreen(
    title: String,
    elements: LazyPagingItems<StatisticsUiElement>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = UiConstants.Spacing.veryLarge
            )
            .padding(top = UiConstants.Spacing.veryLarge)
    ) {
        SettingsStatisticsSectionTitle(title = title)
        if (elements.itemCount > 0) {
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    SettingsStatisticsSectionHeader(
                        element = elements[0]!!,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    LazyColumnCompat {
                        with(SettingsStatisticsSectionFactory) {
                            largeList(elements)
                            playerSpacer()
                        }
                    }
                }
            }
        } else {
            NoElementView()
        }
    }
}
