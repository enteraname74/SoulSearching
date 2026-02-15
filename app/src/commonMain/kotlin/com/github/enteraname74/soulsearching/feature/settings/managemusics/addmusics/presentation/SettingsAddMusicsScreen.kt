package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddNewsSongsStepState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsDataScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsFetchingScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavedSongsScreen

@Composable
fun SettingsAddMusicsRoute(
    viewModel: SettingsAddMusicsViewModel,
    onNavigationState: (SettingsAddMusicsNavigationState) -> Unit,
) {
    SoulScreen {
        val state: AddNewsSongsStepState by viewModel.state.collectAsStateWithLifecycle()
        val navigationState: SettingsAddMusicsNavigationState by viewModel
            .navigationState.collectAsStateWithLifecycle()

        LaunchedEffect(navigationState) {
            onNavigationState(navigationState)
            viewModel.consumeNavigation()
        }

        when (state) {
            is AddNewsSongsStepState.Data -> {
                SettingsAddMusicsDataScreen(
                    navigateBack = viewModel::navigateBack,
                    fetchedMusics = (state as AddNewsSongsStepState.Data).fetchedMusics,
                    toggleMusicSelectedState = viewModel::toggleMusicSelectedState,
                    saveSelectedSongs = viewModel::saveSelectedSongs,
                    fetchSongs = viewModel::fetchSongs,
                )
            }

            AddNewsSongsStepState.Fetching -> {
                SettingsAddMusicsFetchingScreen(
                    navigateBack = viewModel::navigateBack,
                )
            }

            AddNewsSongsStepState.SongsSaved -> {
                SettingsAddMusicsSavedSongsScreen(
                    navigateBack = viewModel::navigateBack,
                    fetchSongs = viewModel::fetchSongs,
                )
            }
        }
    }
}


const val FETCHED_MUSICS_SPACER_CONTENT_TYPE: String = "FETCHED_MUSICS_SPACER_CONTENT_TYPE"
