package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_more_vertical
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun <T> SoulChoiceIconButton(
    choices: List<SoulChoiceButtonData<T>>,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    buttonIcon: DrawableResource = CoreRes.drawable.ic_more_vertical,
    enabled: Boolean = true,
    buttonColors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
    choicesColors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Box {
        SoulIconButton(
            onClick = { isExpanded = true },
            modifier = modifier,
            enabled = enabled,
            icon = buttonIcon,
            colors = buttonColors,
        )
        DropdownMenu(
            expanded = isExpanded,
            containerColor = choicesColors.containerColor,
            onDismissRequest = { isExpanded = false },
        ) {
            choices.forEach { choice ->
                MenuItem(
                    data = choice,
                    contentColor = choicesColors.contentColor,
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
                    color = contentColor,
                )
            }
        },
        onClick = onClick,
    )
}
