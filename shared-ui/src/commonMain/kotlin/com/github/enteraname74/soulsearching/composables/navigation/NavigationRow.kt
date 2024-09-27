package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun NavigationRow(
    navigationRowSpec: NavigationRowSpec
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithHandCursor {
                navigationRowSpec.onClick()
            }
            .padding(
                horizontal = UiConstants.Spacing.large,
                vertical = UiConstants.Spacing.mediumPlus,
            ),
        horizontalArrangement = Arrangement.spacedBy(
            UiConstants.Spacing.large,
        )
    ) {
        SoulIcon(
            modifier = Modifier
                .alpha(
                    if (navigationRowSpec.isSelected) {
                        1f
                    } else {
                        0f
                    }
                ),
            icon = navigationRowSpec.icon,
            tint = SoulSearchingColorTheme.colorScheme.onSecondary,
            contentDescription = null,
        )
        Text(
            text = navigationRowSpec.title,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (navigationRowSpec.isSelected) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Normal
            }
        )
    }
}