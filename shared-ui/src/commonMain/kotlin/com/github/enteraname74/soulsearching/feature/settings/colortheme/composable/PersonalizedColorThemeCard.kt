package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

@Composable
fun PersonalizedColorThemeCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = UiConstants.Spacing.large,
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
        padding = padding
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = UiConstants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
        ) {
            SettingsSwitchElement(
                title = strings.dynamicPlayerView,
                toggleAction = { if (isSelected) togglePersonalizedDynamicPlayerTheme() },
                isChecked = hasPlayerTheme,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = strings.dynamicPlaylistView,
                toggleAction = { if (isSelected) togglePersonalizedDynamicPlaylistTheme() },
                isChecked = hasPlaylistTheme,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = strings.dynamicOtherView,
                toggleAction = { if (isSelected) togglePersonalizedDynamicOtherViewsTheme() },
                isChecked = hasOtherViewsTheme,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
        }
    }
}