package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulChip
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.ListStatistics

@Composable
fun SettingsStatisticsListStatisticsHeader(
    statistics: List<ListStatistics>,
    selectedType: ListStatistics.Type,
    onTypeChange: (ListStatistics.Type) -> Unit,
) {
    LazyRowCompat(
        contentPadding = PaddingValues(
            top = UiConstants.Spacing.medium,
            start = UiConstants.Spacing.large,
            end = UiConstants.Spacing.large,
            bottom = UiConstants.Spacing.medium,
        ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        items(
            items = statistics.map { it.type },
        ) { type ->
            SoulChip(
                title = type.sectionTitle,
                onClick = { onTypeChange(type) },
                isSelected = type == selectedType
            )
        }
    }
}