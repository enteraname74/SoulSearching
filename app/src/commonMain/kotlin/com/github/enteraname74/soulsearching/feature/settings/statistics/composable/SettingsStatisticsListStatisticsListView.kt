package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsListStatisticsCard
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsStatisticsListStatisticsListView(
    listStatistics: List<ListStatistics>,
    modifier: Modifier = Modifier,
    itemsPerRow: Int,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = itemsPerRow,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
    ) {
        listStatistics.forEach {
            SettingsStatisticsListStatisticsCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxRowHeight(),
                listStatistics = it,
                maxItems = 5,
            )
        }
    }
}