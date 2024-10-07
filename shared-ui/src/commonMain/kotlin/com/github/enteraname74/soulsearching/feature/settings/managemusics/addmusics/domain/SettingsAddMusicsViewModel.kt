package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.usecase.folder.GetHiddenFoldersPathUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.soulsearching.coreui.ext.coerceForProgressBar
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SettingsAddMusicsViewModel(
    private val musicFetcher: MusicFetcher,
    private val getHiddenFoldersPathUseCase: GetHiddenFoldersPathUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
) : ScreenModel {
    private var _state: MutableStateFlow<SettingsAddMusicsState> = MutableStateFlow(
        SettingsAddMusicsState.Fetching
    )
    val state: StateFlow<SettingsAddMusicsState> = _state.asStateFlow()

    fun toggleMusicSelectedState(musicId: UUID) {
        (_state.value as? SettingsAddMusicsState.Data)?.fetchedMusics?.let { songs ->
            _state.update { currentState ->
                SettingsAddMusicsState.Data(
                    fetchedMusics = songs.map {
                        if (it.music.musicId == musicId) {
                            it.copy(isSelected = !it.isSelected)
                        } else {
                            it.copy()
                        }
                    }
                )
            }
        }
    }

    /**
     * Fetch and add new musics.
     */
    fun fetchSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths: List<String> = getHiddenFoldersPathUseCase()
            val allMusicsPaths: List<String> = getAllMusicUseCase().first().map { it.path }

            _state.value = SettingsAddMusicsState.Fetching

            val newMusics = musicFetcher.fetchMusicsFromSelectedFolders(
                alreadyPresentMusicsPaths = allMusicsPaths,
                hiddenFoldersPaths = hiddenFoldersPaths
            )
            _state.value = SettingsAddMusicsState.Data(
                fetchedMusics = newMusics,
            )
        }
    }

    fun saveSelectedSongs() {
        val fetchedMusics = (_state.value as? SettingsAddMusicsState.Data)?.fetchedMusics ?: return

        CoroutineScope(Dispatchers.IO).launch {
            _state.value = SettingsAddMusicsState.SavingSongs(
                progress = 0f
            )

            musicFetcher.saveAllMusics(
                musics = fetchedMusics
                    .filter { it.isSelected }
                    .map { it.music },
                onSongSaved = { progress ->
                    _state.value = SettingsAddMusicsState.SavingSongs(
                        progress = progress.coerceForProgressBar()
                    )
                },
            )
            _state.value = SettingsAddMusicsState.SongsSaved
        }
    }
}