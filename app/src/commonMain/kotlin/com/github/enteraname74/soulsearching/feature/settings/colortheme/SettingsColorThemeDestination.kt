package com.github.enteraname74.soulsearching.feature.settings.colortheme

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.presentation.SettingsThemeSelectionDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsColorThemeDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsColorThemeDestination> {
            SettingsColorThemeRoute(
                navigateBack = { navigator.goBack() },
                toThemeSelection = {
                    navigator.navigate(SettingsThemeSelectionDestination)
                }
            )
        }
    }
}