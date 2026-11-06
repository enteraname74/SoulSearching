package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsSmallCard
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsSmallCardSpec

@Composable
fun SettingsStatisticsSmallCardsView(
    specs: List<SettingsStatisticsSmallCardSpec>,
    modifier: Modifier = Modifier,
) {
    val itemsPerRow = if (rememberWindowSize() == WindowSize.Small) 2 else 4

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = itemsPerRow,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
    ) {
        specs.forEach {
            SettingsStatisticsSmallCard(
                modifier = Modifier.weight(1f),
                spec = it,
            )
        }
    }
}