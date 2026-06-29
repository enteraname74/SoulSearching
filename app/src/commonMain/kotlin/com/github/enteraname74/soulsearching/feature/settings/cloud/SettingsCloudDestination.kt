package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.cloud.settings.SettingsCloudSettingsDestination
import com.github.enteraname74.soulsearching.feature.settings.cloud.signin.SettingsCloudSignInDestination
import com.github.enteraname74.soulsearching.feature.settings.cloud.sync.SettingsCloudSyncDestination
import com.github.enteraname74.soulsearching.feature.settings.cloud.user.SettingsCloudUserDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudDestination> {
            val holder: SettingsCloudViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudNavScope {
                    override fun toConnection() {
                        navigator.push(SettingsCloudSignInDestination)
                    }

                    override fun toSettings() {
                        navigator.push(SettingsCloudSettingsDestination)
                    }

                    override fun navigateBack() {
                        navigator.pop()
                    }

                    override fun toUser() {
                        navigator.push(SettingsCloudUserDestination)
                    }

                    override fun toSync() {
                        navigator.push(SettingsCloudSyncDestination)
                    }
                }
            )
        }
    }
}