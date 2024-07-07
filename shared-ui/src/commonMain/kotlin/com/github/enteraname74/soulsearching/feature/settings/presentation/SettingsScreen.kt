package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.composables.SouTopBar
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.personalisation.presentation.SettingsPersonalisationScreen
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.presentation.SettingsAboutScreen
import com.github.enteraname74.soulsearching.feature.settings.colortheme.presentation.SettingsColorThemeScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.SettingsManageMusicsScreen

/**
 * Represent the view of the settings screen.
 */
class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsScreenView(
            finishAction = {
                navigator.pop()
            },
            navigateToAbout = {
                navigator.push(
                    SettingsAboutScreen()
                )
            },
            navigateToColorTheme = {
                navigator.push(
                    SettingsColorThemeScreen()
                )
            },
            navigateToManageMusics = {
                navigator.push(
                    SettingsManageMusicsScreen()
                )
            },
            navigateToPersonalisation = {
                navigator.push(
                    SettingsPersonalisationScreen()
                )
            }
        )
    }
}

@Composable
fun SettingsScreenView(
    finishAction: () -> Unit,
    navigateToManageMusics: () -> Unit,
    navigateToColorTheme: () -> Unit,
    navigateToPersonalisation: () -> Unit,
    navigateToAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SouTopBar(
            title = strings.settings,
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = strings.manageMusicsTitle,
                    text = strings.manageMusicsText,
                    icon = Icons.Rounded.MusicNote,
                    clickAction = navigateToManageMusics
                )
            }
            item {
                SettingsElement(
                    title = strings.colorThemeTitle,
                    text = strings.colorThemeText,
                    icon = Icons.Rounded.Palette,
                    clickAction = navigateToColorTheme
                )
            }
            item {
                SettingsElement(
                    title = strings.personalizationTitle,
                    text = strings.personalizationText,
                    icon = Icons.Rounded.Edit,
                    clickAction = navigateToPersonalisation
                )
            }
            item {
                SettingsElement(
                    title = strings.aboutTitle,
                    text = strings.aboutText,
                    icon = Icons.Rounded.Info,
                    clickAction = navigateToAbout
                )
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}