package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun SoulSegmentedButton(
    leftButtonSpec: SoulSegmentedButtonSpec,
    rightButtonSpec: SoulSegmentedButtonSpec,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        SoulButton(
            onClick = leftButtonSpec.onClick,
            shape = LEFT_BUTTON_SHAPE,
            contentPadding = SoulButtonDefaults.contentPadding(
                horizontal = UiConstants.Spacing.medium,
            )
        ) {
            leftButtonSpec.Content(
                contentColor = colors.contentColor,
            )
        }
        Divider(
            colors = colors,
        )
        SoulButton(
            onClick = rightButtonSpec.onClick,
            shape = RIGHT_BUTTON_SHAPE,
            contentPadding = SoulButtonDefaults.contentPadding(
                horizontal = 0.dp,
            )
        ) {
            rightButtonSpec.Content(
                contentColor = colors.contentColor,
            )
        }
    }
}

private val LEFT_BUTTON_SHAPE = RoundedCornerShape(
    topStartPercent = 50,
    bottomStartPercent = 50,
)

private val RIGHT_BUTTON_SHAPE = RoundedCornerShape(
    topEndPercent = 50,
    bottomEndPercent = 50,
)

private val DIVIDER_THICKNESS: Dp = 1.dp
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
            .background(color = colors.contentColor)
    )
}

sealed interface SoulSegmentedButtonSpec {
    val data: Any
    val onClick: () -> Unit

    @Composable
    fun Content(
        contentColor: Color,
    )
}

data class SoulSegmentedTextButton(
    override val data: String,
    override val onClick: () -> Unit,
) : SoulSegmentedButtonSpec {
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
    val contentDescription: String? = null,
) : SoulSegmentedButtonSpec {

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