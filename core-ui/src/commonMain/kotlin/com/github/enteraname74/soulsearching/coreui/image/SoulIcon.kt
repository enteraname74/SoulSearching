package com.github.enteraname74.soulsearching.coreui.image

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
    badgeColor: Color? = null,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    size: Dp = UiConstants.ImageSize.smallPlus,
) {
    BadgedBox(
        badge = {
            badgeColor?.let {
                Badge(
                    containerColor = it,
                )
            }
        }
    ) {
        Icon(
            modifier = modifier
                .size(size),
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}