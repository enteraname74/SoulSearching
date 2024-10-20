package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuExpand(
    title: String,
    subTitle: String,
    clickAction: () -> Unit,
    clickEnabled: Boolean = true,
    isExpanded: Boolean,
    padding: Dp = UiConstants.Spacing.veryLarge,
    containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    subTextColor: Color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
    content: @Composable () -> Unit,
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(size = RoundedCornerShapeValue)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableIf(enabled = clickEnabled) {
                    clickAction()
                }
                .padding(padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
            ) {
                SoulMenuBody(
                    title = title,
                    text = subTitle,
                    titleColor = textColor,
                    textColor = subTextColor,
                )
            }
            SoulIcon(
                icon = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.rotate(rotation),
            )
        }
        AnimatedVisibility(
            visible = isExpanded
        ) {
            content()
        }
    }
}

private val RoundedCornerShapeValue: Dp = 10.dp