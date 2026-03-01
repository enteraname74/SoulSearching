package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    navigateBack: () -> Unit,
    toAbout: () -> Unit,
    toColorTheme: () -> Unit,
    toManageMusics: () -> Unit,
    toPersonalisation: () -> Unit,
    toStatistics: () -> Unit,
    toAdvancedSettings: () -> Unit,
) {
    val viewModel: SettingsScreenViewModel = koinViewModel()

    val shouldShowNewVersionPin: Boolean by viewModel.shouldShowNewVersionPin.collectAsState()

    SettingsScreenView(
        shouldShowNewVersionPin = shouldShowNewVersionPin,
        finishAction = navigateBack,
        navigateToAbout = toAbout,
        navigateToColorTheme = toColorTheme,
        navigateToManageMusics = toManageMusics,
        navigateToPersonalisation = toPersonalisation,
        navigateToStatistics = toStatistics,
        navigateToAdvanced = toAdvancedSettings,
    )
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
) {
    SettingPage(
        navigateBack = finishAction,
        title = strings.settings,
    ) {
        item {
            SoulMenuElement(
                title = strings.manageMusicsTitle,
                subTitle = strings.manageMusicsText,
                leadIcon = Icons.Rounded.MusicNote,
                onClick = navigateToManageMusics
            )
        }
        item {
            SoulMenuElement(
                title = strings.colorThemeTitle,
                subTitle = strings.colorThemeText,
                leadIcon = Icons.Rounded.Palette,
                onClick = navigateToColorTheme
            )
        }
        item {
            SoulMenuElement(
                title = strings.personalizationTitle,
                subTitle = strings.personalizationText,
                leadIcon = Icons.Rounded.Edit,
                onClick = navigateToPersonalisation
            )
        }
        item {
            SoulMenuElement(
                title = strings.statisticsTitle,
                subTitle = strings.statisticsText,
                leadIcon = Icons.Rounded.BarChart,
                onClick = navigateToStatistics
            )
        }
        item {
            SoulMenuElement(
                title = strings.advancedSettingsTitle,
                subTitle = strings.advancedSettingsText,
                leadIcon = Icons.Rounded.Handyman,
                onClick = navigateToAdvanced,
            )
        }
        item {
            SoulMenuElement(
                title = strings.aboutTitle,
                subTitle = strings.aboutText,
                leadIcon = Icons.Rounded.Info,
                onClick = navigateToAbout,
                isBadged = shouldShowNewVersionPin,
            )
        }
    }
}