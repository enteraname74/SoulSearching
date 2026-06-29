package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.SettingsAboutDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedDestination
import com.github.enteraname74.soulsearching.feature.settings.cloud.SettingsCloudDestination
import com.github.enteraname74.soulsearching.feature.settings.colortheme.SettingsColorThemeDestination
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.SettingsManageMusicsDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.SettingsPersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.SettingsStatisticsDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsDestination> {
            SettingsRoute(
                navigateBack = {
                    navigator.pop()
                },
                toAbout = {
                    navigator.push(SettingsAboutDestination)
                },
                toColorTheme = {
                    navigator.push(SettingsColorThemeDestination)
                },
                toManageMusics = {
                    navigator.push(SettingsManageMusicsDestination)
                },
                toPersonalisation = {
                    navigator.push(SettingsPersonalisationDestination)
                },
                toStatistics = {
                    navigator.push(SettingsStatisticsDestination)
                },
                toAdvancedSettings = {
                    navigator.push(SettingsAdvancedDestination(focusedElement = null))
                },
                toCloudSettings = {
                    navigator.push(SettingsCloudDestination)
                }
            )
        }
    }
}