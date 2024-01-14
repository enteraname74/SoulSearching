package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.soulsearching.Drawables
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.ColorThemeCard
import com.github.soulsearching.composables.settings.PersonalizedColorThemeCard
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.strings
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
            title = strings.colorThemeTitle,
            leftAction = finishAction
        )
        LazyColumn {
            item {
                ColorThemeCard(
                    title = strings.dynamicThemeTitle,
                    text = strings.dynamicThemeText,
                    onClick = { updateColorThemeMethod(ColorThemeType.DYNAMIC) },
                    isSelected = colorThemeManager.colorThemeType == ColorThemeType.DYNAMIC,
                    firstImagePath = Drawables.dynamicMain,
                    secondImagePath = Drawables.dynamicPlayer
                )
            }
            item {
                ColorThemeCard(
                    title = strings.systemThemeTitle,
                    text = strings.systemThemeText,
                    onClick = { updateColorThemeMethod(ColorThemeType.SYSTEM) },
                    isSelected = colorThemeManager.colorThemeType == ColorThemeType.SYSTEM,
                    firstImagePath = if (isSystemInDarkTheme()) {
                        Drawables.systemDarkThemeMain
                    } else {
                        Drawables.systemLightThemeMain
                    },
                    secondImagePath = if (isSystemInDarkTheme()) {
                        Drawables.systemDarkThemePlayer
                    } else {
                        Drawables.systemLightThemePlayer
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