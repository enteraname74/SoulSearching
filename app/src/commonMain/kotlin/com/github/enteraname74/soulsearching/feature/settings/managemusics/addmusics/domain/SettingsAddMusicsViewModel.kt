package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.ext.coerceForProgressBar
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state.SettingsAddMusicsState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.SettingsAddMusicsDestination
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SettingsAddMusicsViewModel(
    private val musicFetcher: MusicFetcher,
    private val commonFolderUseCase: CommonFolderUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val multipleArtistListener: MultipleArtistListener,
    settingsAddMusicsDestination: SettingsAddMusicsDestination,
) : ViewModel() {
    private val shouldShowSaveScreen = settingsAddMusicsDestination.shouldShowSaveScreen

    init {
        if (shouldShowSaveScreen) {
            showSaveScreen()
        } else {
            fetchSongs()
        }
    }

    private val _state: MutableStateFlow<SettingsAddMusicsState> = MutableStateFlow(
        SettingsAddMusicsState.Fetching
    )
    val state: StateFlow<SettingsAddMusicsState> = combine(
        _state,
        multipleArtistListener.state,
    ) { state, multipleArtistListenerState ->
        when (state) {
            is SettingsAddMusicsState.Data -> state
            SettingsAddMusicsState.Fetching -> state
            is SettingsAddMusicsState.SavingSongs -> {
                when (multipleArtistListenerState) {
                    MultipleArtistHandlingStep.Idle -> state
                    MultipleArtistHandlingStep.UserChoice -> SettingsAddMusicsState.Data(state.fetchedMusics)
                    MultipleArtistHandlingStep.SongsSaved -> SettingsAddMusicsState.SongsSaved
                }
            }

            SettingsAddMusicsState.SongsSaved -> state
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsAddMusicsState.Fetching
    )

    private val _navigationState: MutableStateFlow<SettingsAddMusicsNavigationState> =
        MutableStateFlow(
            SettingsAddMusicsNavigationState.Idle
        )
    val navigationState: StateFlow<SettingsAddMusicsNavigationState> =
        _navigationState.asStateFlow()

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

    private fun showSaveScreen() {
        _state.value = SettingsAddMusicsState.SongsSaved
    }

    /**
     * Fetch and add new musics.
     */
    fun fetchSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            multipleArtistListener.consumeStep()

            val hiddenFoldersPaths: List<String> = commonFolderUseCase.getHiddenFoldersPath()
            val allMusicsPaths: List<String> = commonMusicUseCase.getAllMusicPath()

            _state.value = SettingsAddMusicsState.Fetching

            val newMusics: List<SelectableMusicItem> = musicFetcher.fetchMusicsFromSelectedFolders(
                alreadyPresentMusicsPaths = allMusicsPaths,
                hiddenFoldersPaths = hiddenFoldersPaths
            )

            // We save the folders to let the user easily removed unwanted one before adding new songs
            val folders: List<Folder> = newMusics.map {
                Folder(
                    folderPath = it.music.folder,
                    isSelected = true,
                )
            }.distinctBy { it.folderPath }

            commonFolderUseCase.upsertAll(
                allFolders = folders,
            )

            _state.value = SettingsAddMusicsState.Data(
                fetchedMusics = newMusics,
            )
        }
    }

    private fun getMultipleArtists(
        selectedMusics: List<Music>,
    ): List<Artist> =
        selectedMusics
            .flatMap { it.artists }
            .filter { it.isComposedOfMultipleArtists() }
            .distinctBy { it.artistId }

    fun saveSelectedSongs() {
        val fetchedMusics = (state.value as? SettingsAddMusicsState.Data)?.fetchedMusics ?: return
        val selectedMusics = fetchedMusics
            .filter { it.isSelected }
            .map { it.music }

        CoroutineScope(Dispatchers.IO).launch {
            _state.value = SettingsAddMusicsState.SavingSongs(
                progress = 0f,
                fetchedMusics = fetchedMusics,
            )

            musicFetcher.cacheSelectedMusics(
                musics = selectedMusics,
                onSongSaved = { progress ->
                    _state.value = SettingsAddMusicsState.SavingSongs(
                        progress = progress.coerceForProgressBar(),
                        fetchedMusics = fetchedMusics,
                    )
                },
            )

            val multipleArtistManager = FetchAllMultipleArtistManagerImpl(
                optimizedCachedData = musicFetcher.optimizedCachedData,
            )
            if (multipleArtistManager.doMusicsHaveMultipleArtists(musics = selectedMusics)) {
                _navigationState.value = SettingsAddMusicsNavigationState.ToMultipleArtists(
                    multipleArtists = getMultipleArtists(musicFetcher.optimizedCachedData.musicsByPath.values.toList())
                )
            } else {
                MusicPersistence(musicFetcher.optimizedCachedData).saveAll()
                _state.value = SettingsAddMusicsState.SongsSaved
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