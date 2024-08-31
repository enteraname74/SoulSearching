package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

object SoulButtonDefaults {
    val shape: Shape = RoundedCornerShape(percent = 50)

    @Composable
    fun contentPadding(
        vertical: Dp = UiConstants.Spacing.medium,
        horizontal: Dp = UiConstants.Spacing.veryLarge,
    ): PaddingValues = PaddingValues(
        vertical = vertical,
        horizontal = horizontal,
    )

    @Composable
    fun colors(
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    ): SoulButtonColors = SoulButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )

    @Composable
    fun primaryColors(): SoulButtonColors = SoulButtonColors(
        contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        containerColor = SoulSearchingColorTheme.colorScheme.primary
    )

    @Composable
    fun secondaryColors(): SoulButtonColors = SoulButtonColors(
        contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
        containerColor = SoulSearchingColorTheme.colorScheme.secondary,
    )
}

data class SoulButtonColors(
    val contentColor: Color,
    val containerColor: Color,
)
