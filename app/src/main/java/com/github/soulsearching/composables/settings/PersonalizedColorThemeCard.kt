package com.github.soulsearching.composables.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SettingsUtils

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
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.toggleDynamicPlayerTheme() },
                isChecked = SettingsUtils.settingsViewModel.isDynamicPlayerThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = stringResource(id = R.string.dynamic_playlist_view),
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.toggleDynamicPlaylistTheme() },
                isChecked = SettingsUtils.settingsViewModel.isDynamicPlaylistThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
            SettingsSwitchElement(
                title = stringResource(id = R.string.dynamic_other_views),
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.toggleDynamicOtherViewsTheme() },
                isChecked = SettingsUtils.settingsViewModel.isDynamicOtherViewsThemeSelected,
                padding = PaddingValues(0.dp),
                titleFontSize = 16.sp
            )
        }
    }
}