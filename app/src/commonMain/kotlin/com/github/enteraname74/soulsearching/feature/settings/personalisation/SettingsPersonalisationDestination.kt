package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.personalisation.album.SettingsAlbumViewPersonalisationDestination
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
                    override val navigateBack: () -> Unit = navigator::pop
                    override val toMainPagePersonalisation: () -> Unit = {
                        navigator.push(SettingsMainPagePersonalisationDestination)
                    }
                    override val toMusicViewPersonalisation: () -> Unit = {
                        navigator.push(SettingsMusicViewPersonalisationDestination)
                    }

                    override val toAlbumViewPersonalisation: () -> Unit = {
                        navigator.push(SettingsAlbumViewPersonalisationDestination)
                    }

                    override val toPlayerPersonalisation: () -> Unit = {
                        navigator.push(SettingsPlayerPersonalisationDestination)
                    }
                }
            )
        }
    }
}