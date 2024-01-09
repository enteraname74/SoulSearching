package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * View model for managing all artists.
 */
class AllArtistsViewModel(
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val settings: SoulSearchingSettings
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artists = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> artistRepository.getAllArtistsWithMusicsSortByNameAscAsFlow()
                        SortType.ADDED_DATE -> artistRepository.getAllArtistWithMusicsSortByAddedDateAscAsFlow()
                        SortType.NB_PLAYED -> artistRepository.getAllArtistWithMusicsSortByNbPlayedAscAsFlow()
                        else -> artistRepository.getAllArtistsWithMusicsSortByNameAscAsFlow()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> artistRepository.getAllArtistWithMusicsSortByNameDescAsFlow()
                        SortType.ADDED_DATE -> artistRepository.getAllArtistWithMusicsSortByAddedDateDescAsFlow()
                        SortType.NB_PLAYED -> artistRepository.getAllArtistWithMusicsSortByNbPlayedDescAsFlow()
                        else -> artistRepository.getAllArtistWithMusicsSortByNameDescAsFlow()
                    }
                }
                else -> artistRepository.getAllArtistsWithMusicsSortByNameAscAsFlow()
            }
        }
    }.stateIn(
        viewModelScope,
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
        viewModelScope,
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
                    // On supprime d'abord les musiques de l'artiste :
                    for (music in state.value.selectedArtist.musics){
                        musicRepository.deleteMusic(music)
                    }
                    // On supprime ensuite tous les albums de l'artiste :
                    val albumsToDelete = albumRepository.getAllAlbumsFromArtist(
                        artistId = state.value.selectedArtist.artist.artistId
                    )
                    for (album in albumsToDelete) {
                        albumRepository.deleteAlbum(album)
                    }
                    // On supprime enfin l'artiste :
                    artistRepository.deleteArtist(state.value.selectedArtist.artist)
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
                    artistRepository.updateQuickAccessState(
                        newQuickAccessState = !state.value.selectedArtist.artist.isInQuickAccess,
                        artistId = state.value.selectedArtist.artist.artistId
                    )
                }
            }
            else -> {}
        }
    }
}