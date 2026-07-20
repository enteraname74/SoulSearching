package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist.join

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object SettingsCloudSharedListJoinDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator
    ) {
        entryProviderScope.entry<SettingsCloudSharedListJoinDestination> {
            val holder: SettingsCloudSharedListJoinViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudSharedListJoinNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}