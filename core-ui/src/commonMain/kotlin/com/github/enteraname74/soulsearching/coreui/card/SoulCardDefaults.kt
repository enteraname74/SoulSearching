package com.github.enteraname74.soulsearching.coreui.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

object SoulCardDefaults {
    @Composable
    fun colors(
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    ): SoulCardColors = SoulCardColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}

data class SoulCardColors(
    val contentColor: Color,
    val containerColor: Color,
)