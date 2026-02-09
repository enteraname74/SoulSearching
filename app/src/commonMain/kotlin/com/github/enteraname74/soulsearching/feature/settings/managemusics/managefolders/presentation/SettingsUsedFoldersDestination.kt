package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsUsedFoldersDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsUsedFoldersDestination> {
            SettingsUsedFoldersRoute(
                navigateBack = {
                    navigator.goBack()
                }
            )
        }
    }
}