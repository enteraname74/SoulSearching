package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ArtistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AllArtistsViewModel(
    private val settings: SoulSearchingSettings,
    private val getAllArtistWithMusicsSortedUseCase: GetAllArtistWithMusicsSortedUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val upsertArtistUseCase: UpsertArtistUseCase,
): ScreenModel {
    private val _sortType = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_ARTISTS_TYPE_KEY, SortType.NAME
        )
    )
    private val _sortDirection = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_ARTISTS_DIRECTION_KEY, SortDirection.ASC
        )
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artists = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            getAllArtistWithMusicsSortedUseCase(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(ArtistState())
    val state = combine(_artists, _state, _sortDirection, _sortType) { artists, state, sortDirection, sortType ->
        state.copy(
            artists = artists.filter { it.musics.any { music -> !music.isHidden } },
            sortDirection = sortDirection,
            sortType = sortType
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        ArtistState()
    )

    /**
     * Manage artists events.
     */
    fun onArtistEvent(event: ArtistEvent) {
        when (event) {
            ArtistEvent.DeleteArtist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    deleteArtistUseCase(
                        artistWithMusics = _state.value.selectedArtist,
                    )
                }
            }
            is ArtistEvent.SetSelectedArtistWithMusics -> {
                _state.update {
                    it.copy(
                        selectedArtist = event.artistWithMusics
                    )
                }
            }
            is ArtistEvent.BottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            is ArtistEvent.DeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            is ArtistEvent.SetSortDirection -> {
                _sortDirection.value = event.type
                settings.setInt(
                    key = SoulSearchingSettings.SORT_ARTISTS_DIRECTION_KEY,
                    value = event.type
                )
            }
            is ArtistEvent.SetSortType -> {
                _sortType.value = event.type
                settings.setInt(
                    key = SoulSearchingSettings.SORT_ARTISTS_TYPE_KEY,
                    value = event.type
                )
            }
            is ArtistEvent.UpdateQuickAccessState -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val artist = _state.value.selectedArtist.artist
                    upsertArtistUseCase(
                        artist = artist.copy(
                            isInQuickAccess = !artist.isInQuickAccess
                        )
                    )
                }
            }
            else -> {}
        }
    }
}