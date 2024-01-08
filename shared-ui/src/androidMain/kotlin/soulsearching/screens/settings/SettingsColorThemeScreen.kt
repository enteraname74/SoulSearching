package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.SettingsUtils
import com.github.soulsearching.classes.types.ColorThemeType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.setting.ColorThemeCard
import com.github.soulsearching.composables.setting.PersonalizedColorThemeCard
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsColorThemeScreen(
    finishAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.color_theme_title),
            leftAction = finishAction
        )
        LazyColumn {
            item {
                ColorThemeCard(
                    title = stringResource(id = R.string.dynamic_theme_title),
                    text = stringResource(id = R.string.dynamic_theme_text),
                    onClick = { SettingsUtils.settingsViewModel.updateColorTheme(ColorThemeType.DYNAMIC) },
                    isSelected = SettingsUtils.settingsViewModel.colorTheme == ColorThemeType.DYNAMIC,
                    firstImageId = R.drawable.dynamic_main,
                    secondImageId = R.drawable.dynamic_player
                )
            }
            item {
                ColorThemeCard(
                    title = stringResource(id = R.string.system_theme_title),
                    text = stringResource(id = R.string.system_theme_text),
                    onClick = { SettingsUtils.settingsViewModel.updateColorTheme(ColorThemeType.SYSTEM) },
                    isSelected = SettingsUtils.settingsViewModel.colorTheme == ColorThemeType.SYSTEM,
                    firstImageId = if (isSystemInDarkTheme()) {
                        R.drawable.system_dark_theme_main
                    } else {
                        R.drawable.system_light_theme_main
                    },
                    secondImageId = if (isSystemInDarkTheme()) {
                        R.drawable.system_dark_theme_player
                    } else {
                        R.drawable.system_light_theme_player
                    }
                )
            }
            item {
                PersonalizedColorThemeCard(
                    onClick = { SettingsUtils.settingsViewModel.updateColorTheme(ColorThemeType.PERSONALIZED) },
                    isSelected = SettingsUtils.settingsViewModel.colorTheme == ColorThemeType.PERSONALIZED
                )
            }
            item {
                PlayerSpacer()
            }
        }
    }
}