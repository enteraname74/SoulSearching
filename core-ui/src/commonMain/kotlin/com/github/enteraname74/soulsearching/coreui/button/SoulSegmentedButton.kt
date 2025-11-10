package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun SoulSegmentedButton(
    buttons: List<SoulSegmentedButtonSpec<*>>,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
) {
    if (buttons.isEmpty()) return

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InnerButtons(
            buttons = buttons,
            colors = colors,
        )
    }
}

@Composable
private fun InnerButtons(
    buttons: List<SoulSegmentedButtonSpec<*>>,
    colors: SoulButtonColors,
) {
    if (buttons.size == 1) {

        val spec = buttons.first()
        SoulButton(
            onClick = spec.onClick,
            colors = colors,
            contentPadding = spec.contentPadding,
        ) {
            spec.Content(
                contentColor = colors.contentColor,
            )
        }
        return
    }

    buttons.forEachIndexed { index, spec ->
        val shape = when(index) {
            0 -> LEFT_BUTTON_SHAPE
            buttons.lastIndex -> RIGHT_BUTTON_SHAPE
            else -> CENTER_SHAPE
        }

        SoulButton(
            onClick = spec.onClick,
            shape = shape,
            colors = colors,
            contentPadding = spec.contentPadding,
        ) {
            spec.Content(
                contentColor = colors.contentColor,
            )
        }

        if (index != buttons.lastIndex) {
            Divider(
                colors = colors,
            )
        }
    }
}

private val LEFT_BUTTON_SHAPE = RoundedCornerShape(
    topStartPercent = 50,
    bottomStartPercent = 50,
)

private val CENTER_SHAPE = RectangleShape

private val RIGHT_BUTTON_SHAPE = RoundedCornerShape(
    topEndPercent = 50,
    bottomEndPercent = 50,
)

private val DIVIDER_THICKNESS: Dp = 2.dp
private val DIVIDER_PADDING: Dp = 4.dp

@Composable
private fun Divider(
    colors: SoulButtonColors,
) {
    Box(
        modifier = Modifier
            .width(DIVIDER_THICKNESS)
            .fillMaxHeight()
            .background(color = colors.containerColor)
            .padding(vertical = DIVIDER_PADDING)
            .background(color = colors.contentColor)
    )
}

sealed interface SoulSegmentedButtonSpec<T> {
    val data: T
    val onClick: () -> Unit
    val contentPadding: PaddingValues

    @Composable
    fun Content(
        contentColor: Color,
    )
}

data class SoulSegmentedTextButton(
    override val data: String,
    override val onClick: () -> Unit,
    override val contentPadding: PaddingValues,
) : SoulSegmentedButtonSpec<String> {
    @Composable
    override fun Content(
        contentColor: Color,
    ) {
        Text(
            color = contentColor,
            text = data,
            fontWeight = FontWeight.Bold,
        )
    }
}

data class SoulSegmentedIconButton(
    override val data: ImageVector,
    override val onClick: () -> Unit,
    override val contentPadding: PaddingValues,
    val contentDescription: String? = null,
) : SoulSegmentedButtonSpec<ImageVector> {

    @Composable
    override fun Content(
        contentColor: Color,
    ) {
        SoulIcon(
            icon = data,
            tint = contentColor,
            contentDescription = contentDescription,
        )
    }
}