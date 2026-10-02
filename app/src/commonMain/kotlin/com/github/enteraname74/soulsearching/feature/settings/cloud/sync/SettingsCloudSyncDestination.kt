package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudSyncDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudSyncDestination> {
            val holder: SettingsCloudSyncViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudSyncNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}
