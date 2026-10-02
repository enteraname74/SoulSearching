package com.github.enteraname74.soulsearching.feature.settings.cloud.user.data

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudUserDataDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudUserDataDestination> {
            val holder: SettingsCloudUserDataViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudUserDataNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}