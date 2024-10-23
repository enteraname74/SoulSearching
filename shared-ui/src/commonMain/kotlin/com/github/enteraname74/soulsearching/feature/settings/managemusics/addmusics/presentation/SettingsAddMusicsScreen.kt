package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsDataScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsFetchingScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavedSongsScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.screens.SettingsAddMusicsSavingScreen
import java.util.UUID

/**
 * Represent the view of the add musics screen in the settings.
 */
class SettingsAddMusicsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsAddMusicsViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val state: SettingsAddMusicsState by screenModel.state.collectAsState()

        var hasLaunchedSongFetching by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (!hasLaunchedSongFetching) {
                screenModel.fetchSongs()
                hasLaunchedSongFetching = true
            }
        }

        SettingsAddMusicsScreenView(
            state = state,
            navigateBack = navigator::pop,
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
        when(state) {
            is SettingsAddMusicsState.Data -> {
                SettingsAddMusicsDataScreen(
                    navigateBack = navigateBack,
                    fetchedMusics = state.fetchedMusics,
                    toggleMusicSelectedState = toggleMusicSelectedState,
                    saveSelectedSongs = saveSelectedSongs,
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
