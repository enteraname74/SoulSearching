package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.AddNewSongsMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MultipleArtistsChoiceViewModel(
    private val musicFetcher: MusicFetcher,
    private val loadingManager: LoadingManager,
): ScreenModel {
    private val artists: MutableStateFlow<List<ArtistChoice>?> = MutableStateFlow(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MultipleArtistChoiceState> = artists.mapLatest { artists ->
        when {
            artists != null -> MultipleArtistChoiceState.UserAction(artists)
            else -> MultipleArtistChoiceState.Loading
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MultipleArtistChoiceState.Loading,
    )

    private val _navigationState: MutableStateFlow<MultipleArtistsChoiceNavigationState> = MutableStateFlow(
        MultipleArtistsChoiceNavigationState.Idle
    )
    val navigationState: StateFlow<MultipleArtistsChoiceNavigationState> = _navigationState.asStateFlow()

    private var multipleArtists: List<Artist> = emptyList()

    fun loadArtistsChoices(
        multipleArtists: List<Artist>
    ) {
        this.multipleArtists = multipleArtists

        CoroutineScope(Dispatchers.IO).launch {
            artists.value = multipleArtists.ifEmpty {
                FetchAllMultipleArtistManagerImpl(
                    optimizedCachedData = musicFetcher.optimizedCachedData,
                ).getPotentialMultipleArtists()
            }.map {
                ArtistChoice(artist = it)
            }
        }
    }

    fun consumeNavigation() {
        _navigationState.value = MultipleArtistsChoiceNavigationState.Idle
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

                val multipleArtistManager = if (multipleArtists.isEmpty()) {
                    FetchAllMultipleArtistManagerImpl(musicFetcher.optimizedCachedData)
                } else {
                    AddNewSongsMultipleArtistManagerImpl(musicFetcher.optimizedCachedData)
                }

                multipleArtistManager.handleMultipleArtists(artistsToDivide = artistsToDivide)
                MusicPersistence(musicFetcher.optimizedCachedData).saveAll()
            }
            _navigationState.value = MultipleArtistsChoiceNavigationState.Quit
        }
    }
}