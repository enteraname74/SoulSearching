package com.github.enteraname74.soulsearching.coreui.multiselection.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulSelectedIcon(
    colors: SoulSelectedIconColors = SoulSelectedIconDefaults.colors(),
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = colors.containerColor,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        SoulIcon(
            tint = colors.contentColor,
            icon = Icons.Rounded.Done,
            size = (size - ICON_PADDING),
        )
    }
}

data class SoulSelectedIconColors(
    val containerColor: Color,
    val contentColor: Color,
)

object SoulSelectedIconDefaults {
    @Composable
    fun colors(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    ): SoulSelectedIconColors = SoulSelectedIconColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}

private val ICON_PADDING: Dp = 10.dp