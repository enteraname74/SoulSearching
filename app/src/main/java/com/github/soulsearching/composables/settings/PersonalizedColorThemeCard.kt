package com.github.soulsearching.composables.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
                .padding(top = Constants.Spacing.large)
        ) {
            SettingsSwitchElement(
                title = stringResource(id = R.string.dynamic_player_view),
                toggleAction = { if (isSelected) SettingsUtils.settingsViewModel.toggleDynamicPlayer() },
                isChecked = SettingsUtils.settingsViewModel.isDynamicPlayerThemeSelected,
                padding = 0.dp,
                titleFontSize = 16.sp
            )
        }
    }
}