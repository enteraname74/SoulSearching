package com.github.enteraname74.soulsearching.feature.application


import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.enteraname74.soulsearching.PlayerViewScaffold
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.navigation.NavigationPanel
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionScaffold
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionNavigationState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.navigation.MainAppNavigationHandler
import com.github.enteraname74.soulsearching.navigation.MainAppSerializerModule
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object MainAppDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
    ) {
        entryProviderScope.entry<MainAppDestination> {
            MainAppRoute()
        }
    }
}

@Composable
private fun MainAppRoute(
    playbackManager: PlaybackManager = injectElement(),
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = MainAppSerializerModule },
        MainPageDestination,
    )
    val navigator = remember { Navigator(backStack) }

    val viewModel: MainAppViewModel = koinViewModel {
        parametersOf(navigator)
    }
    val state: MainAppState by viewModel.state.collectAsStateWithLifecycle()

    LaunchInit {
        playbackManager.initFromSavedData()
    }

    Row {
        val windowSize = rememberWindowSize()

        if (windowSize == WindowSize.Large) {
            NavigationPanel(rows = state.navigationRows)
        }

        MultiSelectionScaffold(
            onNavigationState = { navigationState ->
                when (navigationState) {
                    MultiSelectionNavigationState.Idle -> {
                        // no-op
                    }
                    is MultiSelectionNavigationState.ToMusicBottomSheet -> {
                        navigator.push(
                            MusicBottomSheetDestination(
                                musicIds = navigationState.musicIds,
                                playlistId = navigationState.playlistId,
                            )
                        )
                    }

                    is MultiSelectionNavigationState.ToPlaylistBottomSheet -> {
                        navigator.push(
                            PlaylistBottomSheetDestination(
                                playlistIds = navigationState.playlistIds,
                            )
                        )
                    }

                    is MultiSelectionNavigationState.ToAlbumBottomSheet -> {
                        navigator.push(
                            AlbumBottomSheetDestination(
                                albumIds = navigationState.albumIds,
                            )
                        )
                    }
                    is MultiSelectionNavigationState.ToArtistBottomSheet -> {
                        navigator.push(
                            ArtistBottomSheetDestination(
                                artistIds = navigationState.artistIds,
                            )
                        )
                    }
                }

            }
        ) {
            PlayerViewScaffold(
                navigator = navigator,
            ) {
                MainAppNavigationHandler(
                    navigator = navigator,
                    backStack = backStack,
                )
            }
        }
    }
}