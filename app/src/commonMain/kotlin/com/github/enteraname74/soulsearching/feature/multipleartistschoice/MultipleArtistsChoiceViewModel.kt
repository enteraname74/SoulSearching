package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddNewsSongsStepManager
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddNewsSongsStepState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.AddNewSongsMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.RepositoryMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MultipleArtistsChoiceViewModel(
    private val musicFetcher: MusicFetcher,
    private val loadingManager: LoadingManager,
    private val addNewsSongsStepManager: AddNewsSongsStepManager,
    destination: MultipleArtistsChoiceDestination,
): ViewModel() {
    val mode = destination.mode
    private val artists: MutableStateFlow<List<ArtistChoice>?> = MutableStateFlow(null)

    val state: StateFlow<MultipleArtistChoiceState> = artists.map { artists ->
        when {
            artists == null ->  MultipleArtistChoiceState.Loading
            artists.isEmpty() -> MultipleArtistChoiceState.NoMultipleArtists
            else -> MultipleArtistChoiceState.UserAction(artists = artists)
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

    init {
        viewModelScope.launch {
            artists.value = when (mode) {
                MultipleArtistsChoiceMode.GeneralCheck -> RepositoryMultipleArtistManagerImpl()
                    .getPotentialMultipleArtists()
                MultipleArtistsChoiceMode.InitialFetch -> FetchAllMultipleArtistManagerImpl(
                    optimizedCachedData = musicFetcher.optimizedCachedData,
                ).getPotentialMultipleArtists()
                is MultipleArtistsChoiceMode.NewSongs -> mode.multipleArtists
            }.sortedBy {
                it.artistName
            }.map {
                ArtistChoice(artist = it)
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = MultipleArtistsChoiceNavigationState.NavigateBack
    }

    fun consumeNavigation() {
        _navigationState.value = MultipleArtistsChoiceNavigationState.Idle
    }

    fun toggleAll(selected: Boolean) {
        artists.update { artists ->
            artists?.map {
                it.copy(isSelected = selected)
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
                    MusicPersistence().saveAll(musicFetcher.optimizedCachedData.musicsByPath.values.toList())
                }
            }

            if (mode is MultipleArtistsChoiceMode.NewSongs) {
                addNewsSongsStepManager.toStep(AddNewsSongsStepState.SongsSaved)
            }
            _navigationState.value = MultipleArtistsChoiceNavigationState.Quit
        }
    }
}