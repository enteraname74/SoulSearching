package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsDataScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsFetchingScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavedSongsScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavingScreen
import java.util.*

@Composable
fun SettingsAddMusicsRoute(
    viewModel: SettingsAddMusicsViewModel,
    onNavigationState: (SettingsAddMusicsNavigationState) -> Unit,
) {

    val state: SettingsAddMusicsState by viewModel.state.collectAsState()
    val navigationState: SettingsAddMusicsNavigationState by viewModel.navigationState.collectAsState()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    SettingsAddMusicsScreenView(
        state = state,
        navigateBack = viewModel::navigateBack,
        fetchSongs = viewModel::fetchSongs,
        toggleMusicSelectedState = viewModel::toggleMusicSelectedState,
        saveSelectedSongs = viewModel::saveSelectedSongs
    )
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
