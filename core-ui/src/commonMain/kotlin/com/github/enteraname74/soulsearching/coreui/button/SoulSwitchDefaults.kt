package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

object SoulSwitchDefaults {

    @Composable
    fun colors(
        thumbColor: Color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    ): SoulSwitchColors = SoulSwitchColors(
        containerColor = containerColor,
        thumbColor = thumbColor,
    )

    @Composable
    fun primaryColors(): SoulSwitchColors = SoulSwitchColors(
        thumbColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
        containerColor = SoulSearchingColorTheme.colorScheme.primary
    )

    @Composable
    fun secondaryColors(): SoulSwitchColors = SoulSwitchColors(
        thumbColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
        containerColor = SoulSearchingColorTheme.colorScheme.secondary,
    )
}

data class SoulSwitchColors(
    val thumbColor: Color,
    val containerColor: Color,
)