package com.github.enteraname74.soulsearching.feature.settings.cloud.user

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.cloud.code.SettingsCloudCodesDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudUserDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudUserDestination> {
            val holder: SettingsCloudUserViewHolder = koinViewModel()
            holder.Screen(
                navigation = object: SettingsCloudUserNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }

                    override fun toCode() {
                        navigator.push(SettingsCloudCodesDestination)
                    }
                }
            )
        }
    }
}
