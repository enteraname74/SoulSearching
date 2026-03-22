package com.github.enteraname74.soulsearching.feature.settings.cloud.code

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudCodeDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudCodeDestination> {
            val holder: SettingsCloudCodeViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudCodeNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }
                }
            )
        }
    }
}