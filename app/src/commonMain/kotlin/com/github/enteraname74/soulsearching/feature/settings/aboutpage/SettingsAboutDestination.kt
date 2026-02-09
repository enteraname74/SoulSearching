package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers.SettingsDevelopersDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsAboutDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsAboutDestination> {
            SettingsAboutRoute(
                navigateBack = { navigator.goBack() },
                toDevelopers = {
                    navigator.navigate(SettingsDevelopersDestination)
                }
            )
        }
    }
}