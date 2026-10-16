package com.github.enteraname74.soulsearching.coreui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.text.SoulText
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SoulChip(
    title: String,
    leadingIcon: DrawableResource?,
    onClick: () -> Unit,
    colors: SoulChipColors = SoulChipColors.default(),
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = colors.border,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .background(colors.container)
            .clickableWithHandCursor { onClick() }
            .padding(
                vertical = UiConstants.Spacing.medium,
                horizontal = UiConstants.Spacing.mediumPlus,
            ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let {
            SoulIcon(
                icon = it,
                color = colors.content,
            )
        }
        SoulText(
            text = title,
            color = colors.content,
            style = UiConstants.Typography.bodySmall,
        )
    }
}

data class SoulChipColors(
    val container: Color,
    val content: Color,
    val border: Color,
) {
    companion object {
        @Composable
        fun default(): SoulChipColors =
            SoulChipColors(
                container = SoulSearchingColorTheme.colorScheme.secondary,
                content = SoulSearchingColorTheme.colorScheme.onSecondary,
                border = Color.Transparent,
            )
    }
}