package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState
import com.github.enteraname74.soulsearching.navigation.NavigationAnimations
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MultipleArtistsChoiceDestination(
    val mode: MultipleArtistsChoiceMode,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<MultipleArtistsChoiceDestination>(
                metadata = NavigationAnimations.horizontal,
            ) { key ->
                MultipleArtistsChoiceRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            MultipleArtistsChoiceNavigationState.Idle -> {
                                /*no-op*/
                            }
                            MultipleArtistsChoiceNavigationState.Quit -> {
                                when (key.mode) {
                                    MultipleArtistsChoiceMode.InitialFetch -> {
                                        navigator.navigate(MainAppDestination) {
                                            clearBackStack = true
                                        }
                                    }
                                    is MultipleArtistsChoiceMode.NewSongs, is MultipleArtistsChoiceMode.GeneralCheck -> {
                                        navigator.goBack()
                                    }
                                }
                            }

                            MultipleArtistsChoiceNavigationState.NavigateBack -> {
                                navigator.goBack()
                            }
                        }
                    }
                )
            }
        }
    }
}