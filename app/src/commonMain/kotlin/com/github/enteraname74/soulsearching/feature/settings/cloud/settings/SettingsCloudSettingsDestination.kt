package com.github.enteraname74.soulsearching.feature.settings.cloud.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudSettingsDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudSettingsDestination> {
            val holder: SettingsCloudSettingsViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudSettingsNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}