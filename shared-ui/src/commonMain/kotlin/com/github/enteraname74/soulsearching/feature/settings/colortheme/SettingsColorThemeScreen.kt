package com.github.enteraname74.soulsearching.feature.settings.colortheme

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
import com.github.enteraname74.soulsearching.coreui.Drawables
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeType
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.ColorThemeCard
import com.github.enteraname74.soulsearching.feature.settings.colortheme.composable.PersonalizedColorThemeCard

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
}

@Composable
fun SettingsColorThemeScreenView(
    finishAction: () -> Unit,
    colorThemeManager: ColorThemeManager = injectElement(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SoulTopBar(
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
                SoulPlayerSpacer()
            }
        }
    }
}