package com.github.enteraname74.soulsearching.coreui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SoulChip(
    title: SoulTextSpec,
    leadingIcon: DrawableResource?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
) {
    val colors = if (isSelected) {
        SoulButtonDefaults.accentColors()
    } else {
        SoulButtonDefaults.secondaryColors()
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .clickableWithHandCursor {
                onClick()
            }
            .background(colors.containerColor)
            .padding(
                vertical = UiConstants.Spacing.mediumPlus,
                horizontal = UiConstants.Spacing.veryLarge,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        leadingIcon?.let {
            SoulIcon(
                icon = leadingIcon,
                color = colors.contentColor
            )
        }
        SoulText(
            text = title,
            color = colors.contentColor,
            style = UiConstants.Typography.body.copy(
                fontWeight = FontWeight.Medium,
            )
        )
    }
}