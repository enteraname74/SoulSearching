package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulSegmentedButton(
    leftButtonSpec: SoulSegmentedButtonSpec,
    rightButtonSpec: SoulSegmentedButtonSpec,
    colors: SoulSegmentedButtonColors = SoulSegmentedButtonDefaults.colors(),
) {
    Row(
        modifier = Modifier
            .background(
                color = colors.containerColor,
                shape = RoundedCornerShape(
                    percent = 50
                )
            ).height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leftButtonSpec.Button(
            contentColor = colors.contentColor
        )
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(DIVIDER_HEIGHT),
            color = colors.contentColor,
            thickness = DIVIDER_THICKNESS
        )
        rightButtonSpec.Button(
            contentColor = colors.contentColor
        )
    }
}

private val DIVIDER_THICKNESS: Dp = 2.dp
private const val DIVIDER_HEIGHT: Float = .8f

object SoulSegmentedButtonDefaults {
    @Composable
    fun colors(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    ): SoulSegmentedButtonColors = SoulSegmentedButtonColors(
        contentColor = contentColor,
        containerColor = containerColor,
    )
}

data class SoulSegmentedButtonColors(
    val containerColor: Color,
    val contentColor: Color,
)

sealed interface SoulSegmentedButtonSpec {
    val data: Any
    val onClick: () -> Unit

    @Composable
    fun Button(
        contentColor: Color,
    )
}

data class SoulSegmentedTextButton(
    override val data: String,
    override val onClick: () -> Unit,
) : SoulSegmentedButtonSpec {
    @Composable
    override fun Button(
        contentColor: Color,
    ) {
        TextButton(
            onClick = onClick
        ) {
            Text(
                color = contentColor,
                text = data,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

data class SoulSegmentedIconButton(
    override val data: ImageVector,
    override val onClick: () -> Unit,
    val contentDescription: String? = null,
) : SoulSegmentedButtonSpec {

    @Composable
    override fun Button(
        contentColor: Color,
    ) {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = data,
                tint = contentColor,
                contentDescription = contentDescription,
            )
        }
    }
}