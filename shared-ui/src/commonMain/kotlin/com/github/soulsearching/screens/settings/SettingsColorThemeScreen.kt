package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.Drawables
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.ColorThemeCard
import com.github.soulsearching.composables.settings.PersonalizedColorThemeCard
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.ColorThemeType

/**
 * Represent the view of the color theme screen.
 */
class SettingsColorThemeScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsColorThemeScreenView(
            finishAction = {
                navigator.pop()
            }
        )
    }

    @Composable
    private fun SettingsColorThemeScreenView(
        finishAction: () -> Unit,
        colorThemeManager: ColorThemeManager = injectElement(),
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
                        onClick = { colorThemeManager.updateColorTheme(ColorThemeType.DYNAMIC) },
                        isSelected = colorThemeManager.colorThemeType == ColorThemeType.DYNAMIC,
                        firstImagePath = Drawables.dynamicMain,
                        secondImagePath = Drawables.dynamicPlayer
                    )
                }
                item {
                    ColorThemeCard(
                        title = strings.systemThemeTitle,
                        text = strings.systemThemeText,
                        onClick = { colorThemeManager.updateColorTheme(ColorThemeType.SYSTEM) },
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
                        onClick = { colorThemeManager.updateColorTheme(ColorThemeType.PERSONALIZED) },
                        isSelected = colorThemeManager.colorThemeType == ColorThemeType.PERSONALIZED
                    )
                }
                item {
                    PlayerSpacer()
                }
            }
        }
    }
}