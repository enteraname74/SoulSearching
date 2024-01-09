package com.github.soulsearching.composables.setting

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.utils.SettingsUtils

@Composable
fun PersonalizedColorThemeCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = Constants.Spacing.large
) {
    ColorCard(
        title = stringResource(id = R.string.personalized_theme_title),
        text = stringResource(id = R.string.personalized_theme_text),
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
                title = stringResource(id = R.string.dynamic_player_view),
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.handler.toggleDynamicPlayerTheme() },
                isChecked = SettingsUtils.settingsViewModel.handler.isDynamicPlayerThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = stringResource(id = R.string.dynamic_playlist_view),
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.handler.toggleDynamicPlaylistTheme() },
                isChecked = SettingsUtils.settingsViewModel.handler.isDynamicPlaylistThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = stringResource(id = R.string.dynamic_other_views),
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.handler.toggleDynamicOtherViewsTheme() },
                isChecked = SettingsUtils.settingsViewModel.handler.isDynamicOtherViewsThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
        }
    }
}