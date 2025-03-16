package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.SettingsAboutScreen
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedScreen
import com.github.enteraname74.soulsearching.feature.settings.cloud.SettingsCloudScreen
import com.github.enteraname74.soulsearching.feature.settings.colortheme.SettingsColorThemeScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.SettingsManageMusicsScreen
import com.github.enteraname74.soulsearching.feature.settings.personalisation.SettingsPersonalisationScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.SettingsStatisticsScreen

/**
 * Represent the view of the settings screen.
 */
class SettingsScreen : Screen, SettingPage {
    @Composable
    override fun Content() {
        val screenModel: SettingsScreenViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        val shouldShowNewVersionPin: Boolean by screenModel.shouldShowNewVersionPin.collectAsState()

        SettingsScreenView(
            shouldShowNewVersionPin = shouldShowNewVersionPin,
            finishAction = {
                navigator.pop()
            },
            navigateToAbout = {
                navigator.safePush(
                    SettingsAboutScreen()
                )
            },
            navigateToColorTheme = {
                navigator.safePush(
                    SettingsColorThemeScreen()
                )
            },
            navigateToManageMusics = {
                navigator.safePush(
                    SettingsManageMusicsScreen()
                )
            },
            navigateToPersonalisation = {
                navigator.safePush(
                    SettingsPersonalisationScreen()
                )
            },
            navigateToStatistics = {
                navigator.safePush(
                    SettingsStatisticsScreen()
                )
            },
            navigateToAdvanced = {
                navigator.safePush(
                    SettingsAdvancedScreen()
                )
            },
            navigateToCloud = {
                navigator.safePush(
                    SettingsCloudScreen()
                )
            }
        )
    }
}

@Composable
fun SettingsScreenView(
    shouldShowNewVersionPin: Boolean,
    finishAction: () -> Unit,
    navigateToManageMusics: () -> Unit,
    navigateToColorTheme: () -> Unit,
    navigateToPersonalisation: () -> Unit,
    navigateToStatistics: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToAdvanced: () -> Unit,
    navigateToCloud: () -> Unit,
) {
    SettingPage(
        navigateBack = finishAction,
        title = strings.settings,
    ) {
        item {
            SoulMenuElement(
                title = strings.manageMusicsTitle,
                subTitle = strings.manageMusicsText,
                icon = Icons.Rounded.MusicNote,
                onClick = navigateToManageMusics
            )
        }
        item {
            SoulMenuElement(
                title = strings.colorThemeTitle,
                subTitle = strings.colorThemeText,
                icon = Icons.Rounded.Palette,
                onClick = navigateToColorTheme
            )
        }
        item {
            SoulMenuElement(
                title = strings.personalizationTitle,
                subTitle = strings.personalizationText,
                icon = Icons.Rounded.Edit,
                onClick = navigateToPersonalisation
            )
        }
        item {
            SoulMenuElement(
                title = strings.statisticsTitle,
                subTitle = strings.statisticsText,
                icon = Icons.Rounded.BarChart,
                onClick = navigateToStatistics
            )
        }
        item {
            SoulMenuElement(
                title = strings.advancedSettingsTitle,
                subTitle = strings.advancedSettingsText,
                icon = Icons.Rounded.Handyman,
                onClick = navigateToAdvanced,
            )
        }
        item {
            SoulMenuElement(
                title = strings.cloudSettingsTitle,
                subTitle = strings.cloudSettingsText,
                icon = Icons.Rounded.Cloud,
                onClick = navigateToCloud
            )
        }
        item {
            SoulMenuElement(
                title = strings.aboutTitle,
                subTitle = strings.aboutText,
                icon = Icons.Rounded.Info,
                onClick = navigateToAbout,
                isBadged = shouldShowNewVersionPin,
            )
        }
    }
}