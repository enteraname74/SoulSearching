package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SoulMenuElement(
    title: String,
    subTitle: String,
    icon: DrawableResource? = null,
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