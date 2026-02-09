package com.github.enteraname74.soulsearching.feature.settings.colortheme.colorseed

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsColorSeedDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsColorSeedDestination> {
            SettingsColorSeedScreen(
                navigateBack = navigator::goBack,
            )
        }
    }
}