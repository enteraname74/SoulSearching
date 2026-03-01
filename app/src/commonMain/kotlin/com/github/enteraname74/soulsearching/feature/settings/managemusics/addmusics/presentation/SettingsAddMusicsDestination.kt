package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parameterSetOf

@Serializable
data object SettingsAddMusicsDestination : SettingPage {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsAddMusicsDestination> { key ->
            SettingsAddMusicsRoute(
                viewModel = koinViewModel { parameterSetOf(key) },
                onNavigationState = { navigationState ->
                    when (navigationState) {
                        SettingsAddMusicsNavigationState.Idle -> {
                            /*no-op*/
                        }

                        is SettingsAddMusicsNavigationState.NavigateBack -> {
                            navigator.goBack()
                        }

                        is SettingsAddMusicsNavigationState.ToMultipleArtists -> {
                            navigator.navigate(
                                MultipleArtistsChoiceDestination(
                                    mode = MultipleArtistsChoiceMode.NewSongs(
                                        multipleArtists = navigationState.multipleArtists
                                    )
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}
