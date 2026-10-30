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
            contentColor = SoulSearchingColorTheme.colorScheme.text,
            containerColor = SoulSearchingColorTheme.colorScheme.background,
            labelColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            selectionContainerColor = SoulSearchingColorTheme.colorScheme.surface.blend(Color.Black, 0.2f),
            selectionContentColor = SoulSearchingColorTheme.colorScheme.text,
        )

    @Composable
    fun secondaryColors(): SoulTextFieldColors =
        SoulTextFieldColors(
            contentColor = SoulSearchingColorTheme.colorScheme.text,
            containerColor = SoulSearchingColorTheme.colorScheme.surface,
            labelColor = SoulSearchingColorTheme.colorScheme.textSecondary,
            selectionContainerColor = SoulSearchingColorTheme.colorScheme.background.blend(Color.Black, 0.2f),
            selectionContentColor = SoulSearchingColorTheme.colorScheme.text,
        )

    internal val FOCUSED_BORDER_SIZE: Dp = 2.dp
}
