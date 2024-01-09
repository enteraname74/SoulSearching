package com.github.soulsearching.composables.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.strings
import com.github.soulsearching.utils.SettingsUtils

@Composable
fun PersonalizedColorThemeCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = Constants.Spacing.large
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
                .padding(top = Constants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.large)
        ) {
            SettingsSwitchElement(
                title = strings.dynamicPlayerView,
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.handler.toggleDynamicPlayerTheme() },
                isChecked = SettingsUtils.settingsViewModel.handler.isDynamicPlayerThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = strings.dynamicPlaylistView,
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.handler.toggleDynamicPlaylistTheme() },
                isChecked = SettingsUtils.settingsViewModel.handler.isDynamicPlaylistThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = strings.dynamicOtherView,
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.handler.toggleDynamicOtherViewsTheme() },
                isChecked = SettingsUtils.settingsViewModel.handler.isDynamicOtherViewsThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
        }
    }
}