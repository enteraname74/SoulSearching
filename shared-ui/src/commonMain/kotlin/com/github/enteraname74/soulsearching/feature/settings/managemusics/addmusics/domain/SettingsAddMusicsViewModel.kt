package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.usecase.folder.GetHiddenFoldersPathUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertAllFoldersUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.soulsearching.coreui.ext.coerceForProgressBar
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SettingsAddMusicsViewModel(
    private val musicFetcher: MusicFetcher,
    private val getHiddenFoldersPathUseCase: GetHiddenFoldersPathUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val upsertAllFoldersUseCase: UpsertAllFoldersUseCase,
) : ScreenModel {
    private var _state: MutableStateFlow<SettingsAddMusicsState> = MutableStateFlow(
        SettingsAddMusicsState.Fetching
    )
    val state: StateFlow<SettingsAddMusicsState> = _state.asStateFlow()

    fun toggleMusicSelectedState(musicId: UUID) {
        (_state.value as? SettingsAddMusicsState.Data)?.fetchedMusics?.let { songs ->
            _state.value = SettingsAddMusicsState.Data(
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

    /**
     * Fetch and add new musics.
     */
    fun fetchSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths: List<String> = getHiddenFoldersPathUseCase()
            val allMusicsPaths: List<String> = getAllMusicUseCase().first().map { it.path }

            _state.value = SettingsAddMusicsState.Fetching

            val newMusics: List<SelectableMusicItem> = musicFetcher.fetchMusicsFromSelectedFolders(
                alreadyPresentMusicsPaths = allMusicsPaths,
                hiddenFoldersPaths = hiddenFoldersPaths
            )

            // We save the folders to let the user easily removed unwanted one before adding new songs
            val folders: List<Folder> = newMusics.map { Folder(it.music.folder) }.distinctBy { it.folderPath }

            upsertAllFoldersUseCase(
                allFolders = folders,
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