package com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.SettingsAddMusicsDestination
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation.SettingsUsedFoldersDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsManageMusicsDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsManageMusicsDestination> {
            SettingsManageMusicsRoute(
                finishAction = {
                    navigator.goBack()
                },
                navigateToFolders = {
                    navigator.navigate(SettingsUsedFoldersDestination)
                },
                navigateToAddMusics = {
                    navigator.navigate(SettingsAddMusicsDestination)
                },
            )
        }
    }
}