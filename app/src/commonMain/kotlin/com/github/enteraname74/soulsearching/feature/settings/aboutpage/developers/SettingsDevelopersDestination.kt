package com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsDevelopersDestination: NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsDevelopersDestination> {
            SettingsDevelopersRoute(
                navigateBack = { navigator.goBack() },
            )
        }
    }
}