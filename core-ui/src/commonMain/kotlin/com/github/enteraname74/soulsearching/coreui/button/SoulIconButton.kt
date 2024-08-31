package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun SoulIconButton(
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: Dp = UiConstants.ImageSize.smallPlus,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = colors.contentColor,
            containerColor = colors.containerColor,
        )
    ) {
        SoulIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = colors.contentColor,
            size = size,
        )
    }
}