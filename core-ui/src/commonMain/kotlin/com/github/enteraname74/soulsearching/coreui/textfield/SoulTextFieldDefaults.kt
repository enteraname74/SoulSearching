package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

object SoulTextFieldDefaults {
    @Composable
    fun primaryColors(): SoulTextFieldColors =
        SoulTextFieldColors(
            contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            containerColor = SoulSearchingColorTheme.colorScheme.primary,
            labelColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
            selectionContainerColor = SoulSearchingColorTheme.colorScheme.secondary.blend(Color.Black, 0.2f),
            selectionContentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
        )

    @Composable
    fun secondaryColors(): SoulTextFieldColors =
        SoulTextFieldColors(
            contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            containerColor = SoulSearchingColorTheme.colorScheme.secondary,
            labelColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
            selectionContainerColor = SoulSearchingColorTheme.colorScheme.primary.blend(Color.Black, 0.2f),
            selectionContentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        )

    internal val FOCUSED_BORDER_SIZE: Dp = 2.dp
}