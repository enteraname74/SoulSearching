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
    val ROUND_SHAPE: Shape = RoundedCornerShape(percent = 50)
    val LIGHT_ROUND_SHAPE: Shape = RoundedCornerShape(percent = 10)

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
    fun primaryColors(
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
        containerColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    ): SoulButtonColors = SoulButtonColors(
        contentColor = contentColor,
        containerColor = containerColor,
    )

    @Composable
    fun secondaryColors(
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    ): SoulButtonColors = SoulButtonColors(
        contentColor = contentColor,
        containerColor = containerColor,
    )
}

data class SoulButtonColors(
    val contentColor: Color,
    val containerColor: Color,
)
