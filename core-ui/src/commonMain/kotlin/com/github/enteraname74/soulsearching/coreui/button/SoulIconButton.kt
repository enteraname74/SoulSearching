package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulIconButton(
    spec: SoulIconButtonSpec,
    colors: SoulIconButtonColors = SoulIconButtonDefaults.colors()
) {
    IconButton(
        modifier = Modifier
            .background(
                color = colors.containerColor,
                shape = CircleShape,
            ),
        onClick = spec.onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = colors.contentColor,
            containerColor = colors.containerColor,
        )
    ) {
        Icon(
            imageVector = spec.icon,
            contentDescription = spec.contentDescription,
            tint = colors.contentColor,
        )
    }
}

object SoulIconButtonDefaults {
    @Composable
    fun colors(
        contentColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
        containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    ): SoulIconButtonColors = SoulIconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}

data class SoulIconButtonSpec(
    val icon: ImageVector,
    val contentDescription: String? = null,
    val onClick: () -> Unit,
)

data class SoulIconButtonColors(
    val contentColor: Color,
    val containerColor: Color,
)