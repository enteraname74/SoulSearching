package com.github.enteraname74.soulsearching.feature.appinit.songfetching

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingNavigationState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object AppInitSongFetchingDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        // TODO: Add slide animation
        entryProviderScope.entry<AppInitSongFetchingDestination> {
            AppInitSongFetchingRoute(
                onNavigationState = {
                    when (it) {
                        AppInitSongFetchingNavigationState.Idle -> {
                            /*no-op*/
                        }

                        AppInitSongFetchingNavigationState.ToMultipleArtists -> {
                            navigator.navigate(
                                MultipleArtistsChoiceDestination(MultipleArtistsChoiceMode.InitialFetch)
                            ) {
                                clearBackStack = true
                            }
                        }
                    }
                }
            )
        }
    }
}