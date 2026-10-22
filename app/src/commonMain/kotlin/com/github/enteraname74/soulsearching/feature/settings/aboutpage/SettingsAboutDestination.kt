package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers.SettingsDevelopersDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsAboutDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsAboutDestination> {
            val holder: SettingsAboutViewHolder = koinViewModel()
            holder.Screen(
                navigation = object : SettingsAboutNavScope {
                    override fun navigateBack() {
                        navigator.pop()
                    }

                    override fun toDevelopers() {
                        navigator.push(SettingsDevelopersDestination)
                    }
                }
            )
        }
    }
}
