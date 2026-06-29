package com.github.enteraname74.soulsearching.feature.settings.cloud.signin

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.cloud.signup.SettingsCloudSignUpDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsCloudSignInDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudSignInDestination> {
            val holder: SettingsCloudSignInViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsCloudSignInNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }

                    override fun toSignUp() {
                        navigator.push(SettingsCloudSignUpDestination)
                    }
                }
            )
        }
    }
}