package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulPageIndicator

@Composable
fun SettingsStatisticsSectionIndicatorList(
    selectedIndex: Int,
    listSize: Int,
    onClick: (selectedIndex: Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        (0 until listSize).forEach { index ->
            SoulPageIndicator(
                isSelected = index == selectedIndex,
                onClick = {
                    onClick(index)
                }
            )
        }
    }
}
