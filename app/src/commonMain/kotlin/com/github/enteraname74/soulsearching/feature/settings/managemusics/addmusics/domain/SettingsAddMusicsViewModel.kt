package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.AddNewSongsMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID

class SettingsAddMusicsViewModel(
    private val musicFetcher: MusicFetcher,
    private val commonFolderUseCase: CommonFolderUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val addNewsSongsStepManager: AddNewsSongsStepManager,
    private val loadingManager: LoadingManager,
) : ViewModel() {
    private val workScope = viewModelScope.plus(Dispatchers.IO)

    val state: StateFlow<AddNewsSongsStepState> = addNewsSongsStepManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AddNewsSongsStepState.Fetching
        )

    private val _navigationState: MutableStateFlow<SettingsAddMusicsNavigationState> =
        MutableStateFlow(
            SettingsAddMusicsNavigationState.Idle
        )

    val navigationState: StateFlow<SettingsAddMusicsNavigationState> =
        _navigationState.asStateFlow()

    fun toggleMusicSelectedState(musicId: UUID) {
        (state.value as? AddNewsSongsStepState.Data)?.fetchedMusics?.let { songs ->
            addNewsSongsStepManager.toStep(
                AddNewsSongsStepState.Data(
                    fetchedMusics = songs.map {
                        if (it.music.musicId == musicId) {
                            it.copy(isSelected = !it.isSelected)
                        } else {
                            it.copy()
                        }
                    }
                )
            )
        }
    }

    init {
        fetchSongs()
    }

    /**
     * Fetch and add new musics.
     */
    fun fetchSongs() {
        workScope.launch {
            addNewsSongsStepManager.toStep(AddNewsSongsStepState.Fetching)

            val hiddenFoldersPaths: List<String> = commonFolderUseCase.getHiddenFoldersPath()
            val allMusicsPaths: List<String> = commonMusicUseCase.getAllMusicPath()

            val newMusics: List<SelectableMusicItem> = musicFetcher.fetchMusicsFromSelectedFolders(
                alreadyPresentMusicsPaths = allMusicsPaths,
                hiddenFoldersPaths = hiddenFoldersPaths
            )

            // We save the folders to let the user easily removed unwanted one before adding new songs
            val folders: List<Folder> =
                newMusics.map { Folder(it.music.folder) }.distinctBy { it.folderPath }

            commonFolderUseCase.upsertAll(
                allFolders = folders,
            )

            addNewsSongsStepManager.toStep(
                AddNewsSongsStepState.Data(
                    fetchedMusics = newMusics,
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        addNewsSongsStepManager.toStep(AddNewsSongsStepState.Fetching)
    }

    private fun getMultipleArtists(
        selectedMusics: List<Music>,
    ): List<Artist> =
        selectedMusics
            .flatMap { it.artists }
            .filter { it.isComposedOfMultipleArtists() }
            .distinctBy { it.artistId }

    fun saveSelectedSongs() {
        val fetchedMusics = (state.value as? AddNewsSongsStepState.Data)?.fetchedMusics ?: return
        val selectedMusics = fetchedMusics
            .filter { it.isSelected }
            .map { it.music }

        loadingManager.withLoadingOnScope(workScope) {
            val musicsToSave: List<Music> = musicFetcher.cacheSelectedMusics(
                musics = selectedMusics,
                onSongSaved = { },
            )

            val multipleArtistManager = AddNewSongsMultipleArtistManagerImpl(
                optimizedCachedData = musicFetcher.optimizedCachedData,
            )
            if (multipleArtistManager.doMusicsHaveMultipleArtists(musics = selectedMusics)) {
                _navigationState.value = SettingsAddMusicsNavigationState.ToMultipleArtists(
                    multipleArtists = getMultipleArtists(musicsToSave)
                )
            } else {
                MusicPersistence().saveAll(musicsToSave)
                addNewsSongsStepManager.toStep(AddNewsSongsStepState.SongsSaved)
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = SettingsAddMusicsNavigationState.NavigateBack
    }

    fun consumeNavigation() {
        _navigationState.value = SettingsAddMusicsNavigationState.Idle
    }
}