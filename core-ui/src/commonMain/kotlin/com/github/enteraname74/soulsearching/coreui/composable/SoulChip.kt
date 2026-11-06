package com.github.enteraname74.soulsearching.coreui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SoulChip(
    title: SoulTextSpec,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: DrawableResource? = null,
    isSelected: Boolean,
    baseColors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
    animated: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        vertical = 10.dp,
        horizontal = UiConstants.Spacing.large,
    ),
) {
    val colors = if (isSelected) {
        SoulButtonDefaults.accentColors()
    } else {
        baseColors
    }

    val containerColor by animateColorAsState(
        targetValue = colors.containerColor,
        animationSpec = tween(
            if (animated) UiConstants.AnimationDuration.normal else 0
        ),
    )
    val contentColor by animateColorAsState(
        targetValue = colors.contentColor,
        animationSpec = tween(
            if (animated) UiConstants.AnimationDuration.normal else 0
        ),
    )

    Row(
        modifier = modifier
            .clip(CircleShape)
            .clickableWithHandCursor {
                onClick()
            }
            .background(containerColor)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        leadingIcon?.let {
            SoulIcon(
                icon = leadingIcon,
                color = contentColor
            )
        }
        SoulText(
            text = title,
            color = contentColor,
            style = UiConstants.Typography.body.copy(
                fontWeight = FontWeight.SemiBold,
            )
        )
    }
}