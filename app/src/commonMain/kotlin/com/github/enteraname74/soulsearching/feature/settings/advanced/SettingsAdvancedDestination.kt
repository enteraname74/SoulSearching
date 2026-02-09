package com.github.enteraname74.soulsearching.feature.settings.advanced

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist.SettingsArtistCoverMethodDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.state.SettingsAdvancedNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SettingsAdvancedDestination(
    val focusedElement: SettingsAdvancedScreenFocusedElement?,
): SettingPage {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SettingsAdvancedDestination> { key ->
                SettingsAdvancedRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigation = { navigationState ->
                        when(navigationState) {
                            SettingsAdvancedNavigationState.Idle -> {
                                /*no-op*/
                            }
                            SettingsAdvancedNavigationState.NavigateBack -> {
                                navigator.goBack()
                            }
                            SettingsAdvancedNavigationState.ToArtistCoverMethod -> {
                                navigator.navigate(SettingsArtistCoverMethodDestination)
                            }
                            SettingsAdvancedNavigationState.ToMultipleArtists -> {
                                navigator.navigate(
                                    MultipleArtistsChoiceDestination(
                                        mode = MultipleArtistsChoiceMode.GeneralCheck,
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
