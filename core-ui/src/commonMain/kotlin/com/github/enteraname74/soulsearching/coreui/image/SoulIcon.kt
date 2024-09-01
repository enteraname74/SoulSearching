package com.github.enteraname74.soulsearching.coreui.image

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    size: Dp = UiConstants.ImageSize.smallPlus,
) {
    Icon(
        modifier = modifier
            .size(size),
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint
    )
}