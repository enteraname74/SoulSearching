package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.folder.GetHiddenFoldersPathUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertAllFoldersUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.soulsearching.coreui.ext.coerceForProgressBar
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class SettingsAddMusicsViewModel(
    private val musicFetcher: MusicFetcher,
    private val getHiddenFoldersPathUseCase: GetHiddenFoldersPathUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val upsertAllFoldersUseCase: UpsertAllFoldersUseCase,
) : ScreenModel {
    private val _state: MutableStateFlow<SettingsAddMusicsState> = MutableStateFlow(
        SettingsAddMusicsState.Fetching
    )
    val state: StateFlow<SettingsAddMusicsState> = _state.asStateFlow()

    private val _navigationState: MutableStateFlow<SettingsAddMusicsNavigationState> = MutableStateFlow(
        SettingsAddMusicsNavigationState.Idle
    )
    val navigationState: StateFlow<SettingsAddMusicsNavigationState> = _navigationState.asStateFlow()

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

    fun showSaveScreen() {
        _state.value = SettingsAddMusicsState.SongsSaved
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

    private fun getMultipleArtists(
        selectedMusics: List<Music>,
    ): List<Artist> {
        val multipleArtistNameOfSelectedMusics = selectedMusics
            .filter { it.hasPotentialMultipleArtists() }
            .map { it.artist }
            .distinct()

        val multipleArtists =  musicFetcher
            .optimizedCachedData
            .artistsByName
            .filter { it.value.artistName in multipleArtistNameOfSelectedMusics }
            .map { it.value }

        return multipleArtists
    }

    fun saveSelectedSongs() {
        val fetchedMusics = (_state.value as? SettingsAddMusicsState.Data)?.fetchedMusics ?: return
        val selectedMusics = fetchedMusics
            .filter { it.isSelected }
            .map { it.music }

        CoroutineScope(Dispatchers.IO).launch {
            _state.value = SettingsAddMusicsState.SavingSongs(
                progress = 0f
            )

            musicFetcher.cacheSelectedMusics(
                musics = selectedMusics,
                onSongSaved = { progress ->
                    _state.value = SettingsAddMusicsState.SavingSongs(
                        progress = progress.coerceForProgressBar()
                    )
                },
            )

            val multipleArtistManager = FetchAllMultipleArtistManagerImpl(
                optimizedCachedData = musicFetcher.optimizedCachedData,
            )
            if (multipleArtistManager.doMusicsHaveMultipleArtists(musics = selectedMusics)) {
                _state.value = SettingsAddMusicsState.Data(
                    fetchedMusics = fetchedMusics,
                )
                _navigationState.value = SettingsAddMusicsNavigationState.ToMultipleArtists(
                    multipleArtists = getMultipleArtists(selectedMusics)
                )
            } else {
                MusicPersistence(musicFetcher.optimizedCachedData).saveAll()
                _state.value = SettingsAddMusicsState.SongsSaved
            }
        }
    }

    fun consumeNavigation() {
        _navigationState.value = SettingsAddMusicsNavigationState.Idle
    }
}