package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.SettingsAboutDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedDestination
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
                    navigator.goBack()
                },
                toAbout = {
                    navigator.navigate(SettingsAboutDestination)
                },
                toColorTheme = {
                    navigator.navigate(SettingsColorThemeDestination)
                },
                toManageMusics = {
                    navigator.navigate(SettingsManageMusicsDestination)
                },
                toPersonalisation = {
                    navigator.navigate(SettingsPersonalisationDestination)
                },
                toStatistics = {
                    navigator.navigate(SettingsStatisticsDestination)
                },
                toAdvancedSettings = {
                    navigator.navigate(SettingsAdvancedDestination(focusedElement = null))
                }
            )
        }
    }
}