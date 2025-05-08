package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun <T>SoulChoiceButton(
    choices: List<SoulChoiceButtonData<T>>,
    currentChoiceName: String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = SoulButtonDefaults.LIGHT_ROUND_SHAPE,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
    contentPadding: PaddingValues = SoulButtonDefaults.contentPadding(),
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Box {
        SoulButton(
            onClick = { isExpanded = true },
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = colors,
            contentPadding = contentPadding,
        ) {
            Text(
                text = currentChoiceName,
                textAlign = TextAlign.Center,
                color = colors.contentColor,
                fontSize = 14.sp
            )
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