package com.github.enteraname74.soulsearching.coreui.topbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

object SoulTopBarDefaults {
    @Composable
    fun primary(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.background,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.text,
        secondaryContent: Color = SoulSearchingColorTheme.colorScheme.textSecondary,
    ): SoulTopBarColors =
        SoulTopBarColors(
            contentColor = contentColor,
            containerColor = containerColor,
            secondaryContent = secondaryContent,
        )

    @Composable
    fun secondary(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.surface,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.text,
        secondaryContent: Color = SoulSearchingColorTheme.colorScheme.textSecondary,
    ): SoulTopBarColors =
        SoulTopBarColors(
            contentColor = contentColor,
            containerColor = containerColor,
            secondaryContent = secondaryContent,
        )
}

data class SoulTopBarColors(
    val containerColor: Color,
    val contentColor: Color,
    val secondaryContent: Color,
)