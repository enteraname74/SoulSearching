package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun SoulSegmentedOptionButton(
    buttons: List<SoulSegmentedOptionButtonSpec<*>>,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
) {
    if (buttons.isEmpty()) return

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(
                shape = RoundedCornerShape(percent = 50)
            )
            .border(
                width = BORDER_THICKNESS,
                color = colors.containerColor,
                shape = RoundedCornerShape(percent = 50)
            ),
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
    buttons: List<SoulSegmentedOptionButtonSpec<*>>,
    colors: SoulButtonColors,
) {
    if (buttons.size == 1) {
        val spec = buttons.first()
        SoulButton(
            onClick = spec.onClick,
            colors = colors.copy(
                containerColor = if (spec.isSelected) {
                    colors.containerColor
                } else {
                    Color.Transparent
                },
            ),
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
            colors = colors.copy(
                containerColor = if (spec.isSelected) {
                    colors.containerColor
                } else {
                    Color.Transparent
                },
            ),
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

private val DIVIDER_THICKNESS: Dp = 2.dp
private const val DIVIDER_HEIGHT: Float = .8f

@Composable
private fun Divider(
    colors: SoulButtonColors,
) {
    Box(
        modifier = Modifier
            .width(DIVIDER_THICKNESS)
            .fillMaxHeight()
            .background(color = colors.containerColor)
            .fillMaxHeight(DIVIDER_HEIGHT)
            .background(color = colors.containerColor)
    )
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

private val BORDER_THICKNESS = 2.dp

sealed interface SoulSegmentedOptionButtonSpec<T> {
    val data: T
    val onClick: () -> Unit
    val isSelected: Boolean
    val contentPadding: PaddingValues

    @Composable
    fun Content(
        contentColor: Color,
    )
}

data class SoulSegmentedOptionTextButton(
    override val data: String,
    override val onClick: () -> Unit,
    override val isSelected: Boolean,
    override val contentPadding: PaddingValues,
): SoulSegmentedOptionButtonSpec<String> {
    @Composable
    override fun Content(
        contentColor: Color,
    ) {
        Text(
            color = contentColor,
            text = data,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

data class SoulSegmentedOptionIconButton(
    override val data: ImageVector,
    override val onClick: () -> Unit,
    override val isSelected: Boolean,
    override val contentPadding: PaddingValues,
    val contentDescription: String? = null,
) : SoulSegmentedOptionButtonSpec<ImageVector> {

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

