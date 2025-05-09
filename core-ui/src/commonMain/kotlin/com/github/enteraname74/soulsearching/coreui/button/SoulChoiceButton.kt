package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun <T> SoulChoiceButton(
    choices: List<SoulChoiceButtonData<T>>,
    currentChoiceName: String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = SoulButtonDefaults.LIGHT_ROUND_SHAPE,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
    contentPadding: PaddingValues = SoulButtonDefaults.contentPadding(
        horizontal = UiConstants.Spacing.medium,
    ),
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val iconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(UiConstants.AnimationDuration.normal),
    )

    Box {
        SoulButton(
            onClick = { isExpanded = true },
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = colors,
            contentPadding = contentPadding,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(
                            weight = 1f,
                            fill = false,
                        ),
                    text = currentChoiceName,
                    color = colors.contentColor,
                    fontSize = 14.sp
                )
                SoulIcon(
                    modifier = Modifier
                        .rotate(iconRotation),
                    icon = Icons.Rounded.ArrowDropDown,
                    tint = colors.contentColor,
                )
            }
        }
        DropdownMenu(
            expanded = isExpanded,
            containerColor = colors.containerColor,
            onDismissRequest = { isExpanded = false },
        ) {
            choices.forEach { choice ->
                MenuItem(
                    data = choice,
                    contentColor = colors.contentColor,
                    onClick = {
                        onClick(choice.data)
                        isExpanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun <T> MenuItem(
    data: SoulChoiceButtonData<T>,
    onClick: () -> Unit,
    contentColor: Color,
) {
    DropdownMenuItem(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand),
        text = {
            Text(
                text = data.title,
                color = contentColor,
                style = UiConstants.Typography.body,
            )
        },
        leadingIcon = data.icon?.let { icon ->
            {
                SoulIcon(
                    icon = icon,
                    tint = contentColor,
                )
            }
        },
        onClick = onClick,
    )
}

data class SoulChoiceButtonData<T>(
    val icon: ImageVector?,
    val data: T,
    val title: String,
)