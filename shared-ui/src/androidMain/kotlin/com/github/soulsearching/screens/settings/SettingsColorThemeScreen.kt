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
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.ColorThemeCard
import com.github.soulsearching.composables.settings.PersonalizedColorThemeCard
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.ColorThemeType

@Composable
fun SettingsColorThemeScreen(
    finishAction: () -> Unit,
    colorThemeManager: ColorThemeManager = injectElement(),
    updateColorThemeMethod: (colorTheme: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
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
                    onClick = { updateColorThemeMethod(ColorThemeType.DYNAMIC) },
                    isSelected = colorThemeManager.colorThemeType == ColorThemeType.DYNAMIC,
                    firstImagePath = "dynamic_main",
                    secondImagePath = "dynamic_player"
                )
            }
            item {
                ColorThemeCard(
                    title = stringResource(id = R.string.system_theme_title),
                    text = stringResource(id = R.string.system_theme_text),
                    onClick = { updateColorThemeMethod(ColorThemeType.SYSTEM) },
                    isSelected = colorThemeManager.colorThemeType == ColorThemeType.SYSTEM,
                    firstImagePath = if (isSystemInDarkTheme()) {
                        "system_dark_theme_main"
                    } else {
                        "system_light_theme_main"
                    },
                    secondImagePath = if (isSystemInDarkTheme()) {
                        "system_dark_theme_player"
                    } else {
                        "system_light_theme_player"
                    }
                )
            }
            item {
                PersonalizedColorThemeCard(
                    onClick = { updateColorThemeMethod(ColorThemeType.PERSONALIZED) },
                    isSelected = colorThemeManager.colorThemeType == ColorThemeType.PERSONALIZED
                )
            }
            item {
                PlayerSpacer()
            }
        }
    }
}