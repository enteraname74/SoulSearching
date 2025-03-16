package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuElement(
    title: String,
    subTitle: String,
    icon: ImageVector? = null,
    isBadged: Boolean = false,
    onClick: (() -> Unit)?,
    padding: PaddingValues = PaddingValues(
        horizontal = UiConstants.Spacing.large,
        vertical = UiConstants.Spacing.veryLarge,
    ),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .optionalClickable(onClick = onClick)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
    ) {
        icon?.let {
            SoulIcon(
                size = UiConstants.ImageSize.medium,
                icon = it,
                badgeColor = if (isBadged) {
                    SoulSearchingColorTheme.colorScheme.onPrimary
                } else {
                    null
                },
            )
        }
        SoulMenuBody(
            title = title,
            text = subTitle,
        )
    }
}