package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.usecase.music.SaveInitialFetchedMusicsUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddNewsSongsStepManager
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddNewsSongsStepState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.AddNewSongsMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.RepositoryMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MultipleArtistsChoiceViewHolder(
    private val musicFetcher: MusicFetcher,
    private val loadingManager: LoadingManager,
    private val addNewsSongsStepManager: AddNewsSongsStepManager,
    destination: MultipleArtistsChoiceDestination,
    private val workDispatcher: WorkDispatcher,
    private val saveInitialFetchedMusicsUseCase: SaveInitialFetchedMusicsUseCase,
) : SoulViewModelHolderV2<MultipleArtistsChoiceNavScope, MultipleArtistChoiceState>() {

    override fun getInitialState(): MultipleArtistChoiceState = MultipleArtistChoiceState.Loading

    val mode: MultipleArtistsChoiceMode = destination.mode
    private val artists: MutableStateFlow<List<ArtistChoice>?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            artists.collectLatest { artists ->
                updateState {
                    when {
                        artists == null -> MultipleArtistChoiceState.Loading
                        artists.isEmpty() -> MultipleArtistChoiceState.NoMultipleArtists(
                            navigateBack = { navigate { navigateBack() } },
                        )
                        else -> MultipleArtistChoiceState.UserAction(
                            artists = artists,
                            onSaveSelection = ::saveSelection,
                            navigateBack = { navigate { navigateBack() } }.takeIf { mode !is MultipleArtistsChoiceMode.InitialFetch },
                            onToggleAll = ::toggleAll,
                            onToggleArtistChoice = ::toggleSelection,
                        )
                    }
                }
            }
        }

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
        if (currentState !is MultipleArtistChoiceState.UserAction) {
            return
        }

        CoroutineScope(workDispatcher.dispatcher).launch {
            loadingManager.withLoading {
                val artistsToDivide: List<Artist> = (currentState as MultipleArtistChoiceState.UserAction)
                    .artists
                    .filter { it.isSelected }
                    .map { it.artist }

                val multipleArtistManager = when (mode) {
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
                    saveInitialFetchedMusicsUseCase(musicFetcher.optimizedCachedData.musicsByPath.values.toList())
                }
            }

            if (mode is MultipleArtistsChoiceMode.NewSongs) {
                addNewsSongsStepManager.toStep(AddNewsSongsStepState.SongsSaved)
            }

            navigate {
                when (mode) {
                    MultipleArtistsChoiceMode.InitialFetch -> {
                        toApp()
                    }
                    is MultipleArtistsChoiceMode.NewSongs, is MultipleArtistsChoiceMode.GeneralCheck -> {
                        navigateBack()
                    }
                }
            }
        }
    }

    @Composable
    override fun Content(state: MultipleArtistChoiceState) {
        MultipleArtistsChoiceScreen(state)
    }
}
