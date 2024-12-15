package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistChoiceState
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.MultipleArtistsChoiceNavigationState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
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

    fun loadArtistsChoices(
        multipleArtists: List<Artist>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            artists.value = musicFetcher.getPotentialMultipleArtist().map {
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
                musicFetcher.saveAllWithMultipleArtists(
                    artistsToDivide = (state.value as MultipleArtistChoiceState.UserAction)
                        .artists
                        .filter { it.isSelected }
                        .map { it.artist }
                )
            }
            _navigationState.value = MultipleArtistsChoiceNavigationState.ToMainScreen
        }
    }
}