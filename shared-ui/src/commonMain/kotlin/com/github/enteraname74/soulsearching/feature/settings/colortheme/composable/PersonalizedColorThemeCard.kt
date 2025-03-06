package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun PersonalizedColorThemeCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    hasPlayerTheme: Boolean,
    hasPlaylistTheme: Boolean,
    hasOtherViewsTheme: Boolean,
    togglePersonalizedDynamicPlayerTheme: () -> Unit,
    togglePersonalizedDynamicPlaylistTheme: () -> Unit,
    togglePersonalizedDynamicOtherViewsTheme: () -> Unit,
) {
    ColorCard(
        title = strings.personalizedThemeTitle,
        text = strings.personalizedThemeText,
        onClick = onClick,
        isSelected = isSelected,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = UiConstants.Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            SoulMenuSwitch(
                title = strings.dynamicPlayerView,
                toggleAction = { if (isSelected) togglePersonalizedDynamicPlayerTheme() },
                isChecked = hasPlayerTheme,
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                padding = PaddingValues(all = 0.dp),
            )
            SoulMenuSwitch(
                title = strings.dynamicPlaylistView,
                toggleAction = { if (isSelected) togglePersonalizedDynamicPlaylistTheme() },
                isChecked = hasPlaylistTheme,
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                padding = PaddingValues(all = 0.dp),
            )
            SoulMenuSwitch(
                title = strings.dynamicOtherView,
                toggleAction = { if (isSelected) togglePersonalizedDynamicOtherViewsTheme() },
                isChecked = hasOtherViewsTheme,
                titleColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                textColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                padding = PaddingValues(all = 0.dp),
            )
        }
    }
}