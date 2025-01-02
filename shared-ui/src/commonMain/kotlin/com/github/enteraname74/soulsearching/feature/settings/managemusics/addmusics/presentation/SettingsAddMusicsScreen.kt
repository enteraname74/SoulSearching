package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceScreen
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsDataScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsFetchingScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavedSongsScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavingScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.SettingsManageMusicsScreen
import java.util.*

/**
 * Represent the view of the add musics screen in the settings.
 */
class SettingsAddMusicsScreen(
    private val shouldShowSaveScreen: Boolean = false,
) : Screen, SettingPage {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsAddMusicsViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: SettingsAddMusicsState by screenModel.state.collectAsState()
        val navigationState: SettingsAddMusicsNavigationState by screenModel.navigationState.collectAsState()

        LaunchInit {
            if (shouldShowSaveScreen) {
                screenModel.showSaveScreen()
            } else {
                screenModel.fetchSongs()
            }
        }

        LaunchedEffect(navigationState) {
            when (navigationState) {
                SettingsAddMusicsNavigationState.Idle -> {
                    /*no-op*/
                }

                is SettingsAddMusicsNavigationState.ToMultipleArtists -> {
                    navigator.safePush(
                        MultipleArtistsChoiceScreen(
                            serializedMode = MultipleArtistsChoiceMode.NewSongs(
                                multipleArtists =
                                (navigationState as SettingsAddMusicsNavigationState.ToMultipleArtists)
                                    .multipleArtists
                            ).serialize(),
                        )
                    )
                    screenModel.consumeNavigation()
                }
            }
        }

        SettingsAddMusicsScreenView(
            state = state,
            navigateBack = { navigator.popUntil { it is SettingsManageMusicsScreen } },
            fetchSongs = screenModel::fetchSongs,
            toggleMusicSelectedState = screenModel::toggleMusicSelectedState,
            saveSelectedSongs = screenModel::saveSelectedSongs
        )
    }
}

@Composable
fun SettingsAddMusicsScreenView(
    state: SettingsAddMusicsState,
    navigateBack: () -> Unit,
    fetchSongs: () -> Unit,
    toggleMusicSelectedState: (musicId: UUID) -> Unit,
    saveSelectedSongs: () -> Unit
) {
    SoulScreen {
        when (state) {
            is SettingsAddMusicsState.Data -> {
                SettingsAddMusicsDataScreen(
                    navigateBack = navigateBack,
                    fetchedMusics = state.fetchedMusics,
                    toggleMusicSelectedState = toggleMusicSelectedState,
                    saveSelectedSongs = saveSelectedSongs,
                    fetchSongs = fetchSongs,
                )
            }

            SettingsAddMusicsState.Fetching -> {
                SettingsAddMusicsFetchingScreen(
                    navigateBack = navigateBack,
                )
            }

            is SettingsAddMusicsState.SavingSongs -> {
                SettingsAddMusicsSavingScreen(
                    progress = state.progress
                )
            }

            SettingsAddMusicsState.SongsSaved -> {
                SettingsAddMusicsSavedSongsScreen(
                    navigateBack = navigateBack,
                    fetchSongs = fetchSongs,
                )
            }
        }
    }
}


const val FETCHED_MUSICS_SPACER_CONTENT_TYPE: String = "FETCHED_MUSICS_SPACER_CONTENT_TYPE"
