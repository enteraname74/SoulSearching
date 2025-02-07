package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SettingsStatisticsSectionTitle(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SoulSearchingColorTheme.colorScheme.primary,
            ),
        text = title,
        color = SoulSearchingColorTheme.colorScheme.onPrimary,
        style = UiConstants.Typography.title,
    )
}