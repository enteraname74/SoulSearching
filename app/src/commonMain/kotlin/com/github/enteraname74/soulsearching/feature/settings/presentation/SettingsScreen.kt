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
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_cloud_filled
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
    toCloudSettings: () -> Unit,
) {
    val viewModel: SettingsScreenViewModel = koinViewModel()

    val shouldShowNewVersionPin: Boolean by viewModel.shouldShowNewVersionPin.collectAsState()

    SettingPage(
        navigateBack = navigateBack,
        title = strings.settings,
    ) {
        item {
            SoulMenuElement(
                title = strings.manageMusicsTitle,
                subTitle = strings.manageMusicsText,
                leadIcon = CoreRes.drawable.ic_music_note_filled,
                onClick = toManageMusics
            )
        }
        item {
            SoulMenuElement(
                title = strings.colorThemeTitle,
                subTitle = strings.colorThemeText,
                leadIcon = CoreRes.drawable.ic_palette_filled,
                onClick = toColorTheme
            )
        }
        item {
            SoulMenuElement(
                title = strings.personalizationTitle,
                subTitle = strings.personalizationText,
                leadIcon = CoreRes.drawable.ic_edit_filled,
                onClick = toPersonalisation
            )
        }
        item {
            SoulMenuElement(
                title = strings.statisticsTitle,
                subTitle = strings.statisticsText,
                leadIcon =  CoreRes.drawable.ic_bar_chart,
                onClick = toStatistics
            )
        }
        item {
            SoulMenuElement(
                title = strings.advancedSettingsTitle,
                subTitle = strings.advancedSettingsText,
                leadIcon =  CoreRes.drawable.ic_handyman_filled,
                onClick = toAdvancedSettings,
            )
        }
        item {
            SoulMenuElement(
                title = strings.cloudTitle,
                subTitle = strings.cloudText,
                leadIcon =  CoreRes.drawable.ic_cloud_filled,
                onClick = toCloudSettings,
            )
        }
        item {
            SoulMenuElement(
                title = strings.aboutTitle,
                subTitle = strings.aboutText,
                leadIcon =  CoreRes.drawable.ic_info_filled,
                onClick = toAbout,
                isBadged = shouldShowNewVersionPin,
            )
        }
    }
}