package com.github.enteraname74.soulsearching.coreui.image

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Deprecated("Use new version")
@Composable
fun SoulIconLegacy(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    badgeColor: Color? = null,
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

@Composable
fun SoulIcon(
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    badgeColor: Color? = null,
    contentDescription: String? = null,
    color: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
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
                .mirrorIfNeeded()
                .size(size),
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = color
        )
    }
}

@Composable
private fun Modifier.mirrorIfNeeded(): Modifier {
    return if (LocalLayoutDirection.current == LayoutDirection.Rtl)
        this.scale(scaleX = -1f, scaleY = 1f)
    else
        this
}