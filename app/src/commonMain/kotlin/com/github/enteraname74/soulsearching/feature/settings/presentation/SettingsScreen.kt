package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.domain.model.SoulPlatform
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_bar_chart
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_cloud_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_handyman_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_keyboard
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_palette_filled
import com.github.enteraname74.soulsearching.coreui.ext.isMouseAndKeyboardOnly
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import org.koin.compose.viewmodel.koinViewModel
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

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
    toShortcuts: () -> Unit,
) {
    val viewModel: SettingsScreenViewModel = koinViewModel()

    val shouldShowNewVersionPin: Boolean by viewModel.shouldShowNewVersionPin.collectAsState()
    val isMouseAndKeyboardOnly = SoulPlatformUtils.isMouseAndKeyboardOnly()

    SettingPage(
        navigateBack = navigateBack,
        title = CoreRes.string.settings.toTextSpec(),
    ) {
        if (SoulPlatformUtils.platform != SoulPlatform.Web) {
            item {
                SoulMenuElement(
                    title = CoreRes.string.manage_musics_title.toTextSpec(),
                    subTitle = CoreRes.string.manage_musics_text.toTextSpec(),
                    leadIcon = CoreRes.drawable.ic_music_note_filled,
                    onClick = toManageMusics
                )
            }
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.color_theme_title.toTextSpec(),
                subTitle = CoreRes.string.color_theme_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_palette_filled,
                onClick = toColorTheme
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.personalization_title.toTextSpec(),
                subTitle = CoreRes.string.personalization_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_edit_filled,
                onClick = toPersonalisation
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.statistics_title.toTextSpec(),
                subTitle = CoreRes.string.statistics_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_bar_chart,
                onClick = toStatistics
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.advanced_settings_title.toTextSpec(),
                subTitle = CoreRes.string.advanced_settings_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_handyman_filled,
                onClick = toAdvancedSettings,
            )
        }
        if (isMouseAndKeyboardOnly) {
            item {
                SoulMenuElement(
                    title = CoreRes.string.shortcuts_title.toTextSpec(),
                    subTitle = CoreRes.string.shortcuts_text.toTextSpec(),
                    leadIcon = CoreRes.drawable.ic_keyboard,
                    onClick = toShortcuts,
                )
            }
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.cloud_title.toTextSpec(),
                subTitle = CoreRes.string.cloud_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_cloud_filled,
                onClick = toCloudSettings,
            )
        }
        item {
            SoulMenuElement(
                title = CoreRes.string.about_title.toTextSpec(),
                subTitle = CoreRes.string.about_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_info_filled,
                onClick = toAbout,
                isBadged = shouldShowNewVersionPin,
            )
        }
    }
}
