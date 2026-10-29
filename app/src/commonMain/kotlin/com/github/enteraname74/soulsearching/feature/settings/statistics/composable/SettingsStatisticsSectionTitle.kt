package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.strings.SoulText

@Composable
fun SettingsStatisticsSectionTitle(title: SoulTextSpec) {
    SoulText(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SoulSearchingColorTheme.colorScheme.primary,
            ),
        text = title,
        color = SoulSearchingColorTheme.colorScheme.onPrimary,
        style = UiConstants.Typography.titleSmall,
    )
}
