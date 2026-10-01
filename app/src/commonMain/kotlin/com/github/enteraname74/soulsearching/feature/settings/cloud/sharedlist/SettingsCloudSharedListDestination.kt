package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist.join.SettingsCloudSharedListJoinDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudSharedListDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudSharedListDestination> {
            val holder: SettingsCloudSharedListViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudSharedListNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }

                    override fun toJoin() {
                        navigator.push(SettingsCloudSharedListJoinDestination)
                    }
                }
            )
        }
    }
}
