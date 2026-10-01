package com.github.enteraname74.soulsearching.feature.settings.shortcuts

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsShortcutsDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsShortcutsDestination> {
            val holder: SettingsShortcutsViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsShortcutsNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}