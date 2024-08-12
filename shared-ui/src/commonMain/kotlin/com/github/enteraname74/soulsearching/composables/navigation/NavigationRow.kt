package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun NavigationRow(
    navigationRowSpec: NavigationRowSpec
) {
    Row(
        modifier = Modifier
            .clickable {
                navigationRowSpec.onClick()
            },
        horizontalArrangement = Arrangement.spacedBy(
            UiConstants.Spacing.large,
        )
    ) {
        Icon(
            imageVector = navigationRowSpec.icon,
            tint = SoulSearchingColorTheme.colorScheme.onSecondary,
            contentDescription = null,
        )
        Text(
            text = navigationRowSpec.title,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}