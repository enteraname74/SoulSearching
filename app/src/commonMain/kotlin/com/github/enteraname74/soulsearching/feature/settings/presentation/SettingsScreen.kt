package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_bar_chart
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_handyman_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_palette_filled
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
                leadIcon = CoreRes.drawable.ic_music_note_filled,
                onClick = navigateToManageMusics
            )
        }
        item {
            SoulMenuElement(
                title = strings.colorThemeTitle,
                subTitle = strings.colorThemeText,
                leadIcon = CoreRes.drawable.ic_palette_filled,
                onClick = navigateToColorTheme
            )
        }
        item {
            SoulMenuElement(
                title = strings.personalizationTitle,
                subTitle = strings.personalizationText,
                leadIcon = CoreRes.drawable.ic_edit_filled,
                onClick = navigateToPersonalisation
            )
        }
        item {
            SoulMenuElement(
                title = strings.statisticsTitle,
                subTitle = strings.statisticsText,
                leadIcon =  CoreRes.drawable.ic_bar_chart,
                onClick = navigateToStatistics
            )
        }
        item {
            SoulMenuElement(
                title = strings.advancedSettingsTitle,
                subTitle = strings.advancedSettingsText,
                leadIcon =  CoreRes.drawable.ic_handyman_filled,
                onClick = navigateToAdvanced,
            )
        }
        item {
            SoulMenuElement(
                title = strings.aboutTitle,
                subTitle = strings.aboutText,
                leadIcon =  CoreRes.drawable.ic_info_filled,
                onClick = navigateToAbout,
                isBadged = shouldShowNewVersionPin,
            )
        }
    }
}