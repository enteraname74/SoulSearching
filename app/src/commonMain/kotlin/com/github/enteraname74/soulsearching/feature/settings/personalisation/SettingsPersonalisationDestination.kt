package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.presentation.SettingsMainPagePersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.music.SettingsMusicViewPersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.presentation.SettingsPlayerPersonalisationDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsPersonalisationDestination: SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsPersonalisationDestination> {
            SettingsPersonalisationRoute(
                navScope = object : SettingsPersonalisationNavigationScope {
                    override val navigateBack: () -> Unit = navigator::goBack
                    override val toMainPagePersonalisation: () -> Unit = {
                        navigator.navigate(SettingsMainPagePersonalisationDestination)
                    }
                    override val toMusicViewPersonalisation: () -> Unit = {
                        navigator.navigate(SettingsMusicViewPersonalisationDestination)
                    }
                    override val toPlayerPersonalisation: () -> Unit = {
                        navigator.navigate(SettingsPlayerPersonalisationDestination)
                    }
                }
            )
        }
    }
}