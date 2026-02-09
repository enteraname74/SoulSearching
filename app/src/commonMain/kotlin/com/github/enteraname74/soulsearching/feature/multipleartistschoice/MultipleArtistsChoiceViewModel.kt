package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.MultipleArtistHandlingStep
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.MultipleArtistListener
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.AddNewSongsMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.RepositoryMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MultipleArtistsChoiceViewModel(
    private val musicFetcher: MusicFetcher,
    private val loadingManager: LoadingManager,
    private val multipleArtistListener: MultipleArtistListener,
    destination: MultipleArtistsChoiceDestination,
): ViewModel() {
    val mode = destination.mode
    private val artists: MutableStateFlow<List<ArtistChoice>?> = MutableStateFlow(null)
    private val toggleState: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val state: StateFlow<MultipleArtistChoiceState> = combine(
        artists,
        toggleState
    ) { artists, toggleState ->
        when {
            artists != null -> {
                if (artists.isEmpty()) {
                    MultipleArtistChoiceState.NoMultipleArtists
                } else {
                    MultipleArtistChoiceState.UserAction(
                        toggleAllState = toggleState,
                        artists = artists,
                    )
                }
            }
            else -> MultipleArtistChoiceState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MultipleArtistChoiceState.Loading,
    )

    private val _navigationState: MutableStateFlow<MultipleArtistsChoiceNavigationState> = MutableStateFlow(
        MultipleArtistsChoiceNavigationState.Idle
    )
    val navigationState: StateFlow<MultipleArtistsChoiceNavigationState> = _navigationState.asStateFlow()

    fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            artists.value = when (mode) {
                MultipleArtistsChoiceMode.GeneralCheck -> RepositoryMultipleArtistManagerImpl()
                    .getPotentialMultipleArtists()
                MultipleArtistsChoiceMode.InitialFetch -> FetchAllMultipleArtistManagerImpl(
                    optimizedCachedData = musicFetcher.optimizedCachedData,
                ).getPotentialMultipleArtists()
                is MultipleArtistsChoiceMode.NewSongs -> mode.multipleArtists
            }.map {
                ArtistChoice(artist = it)
            }

            if (mode is MultipleArtistsChoiceMode.NewSongs) {
                // To fix clipping from previous screen when changing state while going to this screen
                delay(500)
                multipleArtistListener.toStep(MultipleArtistHandlingStep.UserChoice)
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = MultipleArtistsChoiceNavigationState.NavigateBack
    }

    fun consumeNavigation() {
        _navigationState.value = MultipleArtistsChoiceNavigationState.Idle
    }

    fun toggleAll() {
        toggleState.value = !toggleState.value
        artists.update { artists ->
            artists?.map {
                it.copy(isSelected = toggleState.value)
            }
        }
    }

    fun toggleSelection(
        artistChoice: ArtistChoice,
    ) {
        artists.update { artists ->
            artists?.map {
                if (it.artist.artistId == artistChoice.artist.artistId) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
        }
    }

    fun saveSelection() {
        if (state.value !is MultipleArtistChoiceState.UserAction) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                val artistsToDivide: List<Artist> = (state.value as MultipleArtistChoiceState.UserAction)
                    .artists
                    .filter { it.isSelected }
                    .map { it.artist }

                val multipleArtistManager = when(mode) {
                    MultipleArtistsChoiceMode.GeneralCheck -> RepositoryMultipleArtistManagerImpl()
                    MultipleArtistsChoiceMode.InitialFetch -> FetchAllMultipleArtistManagerImpl(
                        optimizedCachedData = musicFetcher.optimizedCachedData,
                    )
                    is MultipleArtistsChoiceMode.NewSongs -> AddNewSongsMultipleArtistManagerImpl(
                        optimizedCachedData = musicFetcher.optimizedCachedData,
                    )
                }

                multipleArtistManager.handleMultipleArtists(artistsToDivide = artistsToDivide)
                if (mode != MultipleArtistsChoiceMode.GeneralCheck) {
                    MusicPersistence(musicFetcher.optimizedCachedData).saveAll()
                }
            }

            if (mode is MultipleArtistsChoiceMode.NewSongs) {
                multipleArtistListener.toStep(MultipleArtistHandlingStep.SongsSaved)
            }
            _navigationState.value = MultipleArtistsChoiceNavigationState.Quit
        }
    }
}