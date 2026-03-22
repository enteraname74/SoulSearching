package com.github.enteraname74.soulsearching.feature.settings.cloud.signup

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudSignUpDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudSignUpDestination> {
            val holder: SettingsCloudSignUpViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudSignUpNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }

                    override fun toSignIn() {
                        // TODO
                    }
                }
            )
        }
    }
}