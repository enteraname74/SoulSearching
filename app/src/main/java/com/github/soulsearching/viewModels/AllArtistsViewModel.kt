package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.states.ArtistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllArtistsViewModel @Inject constructor(
    private val artistDao: ArtistDao,
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artists = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> artistDao.getAllArtistsWithMusicsSortByNameAsc()
                        SortType.ADDED_DATE -> artistDao.getAllArtistWithMusicsSortByAddedDateAsc()
                        SortType.NB_PLAYED -> artistDao.getAllArtistWithMusicsSortByNbPlayedAsc()
                        else -> artistDao.getAllArtistsWithMusicsSortByNameAsc()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> artistDao.getAllArtistWithMusicsSortByNameDesc()
                        SortType.ADDED_DATE -> artistDao.getAllArtistWithMusicsSortByAddedDateDesc()
                        SortType.NB_PLAYED -> artistDao.getAllArtistWithMusicsSortByNbPlayedDesc()
                        else -> artistDao.getAllArtistWithMusicsSortByNameDesc()
                    }
                }
                else -> artistDao.getAllArtistsWithMusicsSortByNameAsc()
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
            artists = artists,
            sortDirection = sortDirection,
            sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ArtistState()
    )

    fun onArtistEvent(event: ArtistEvent) {
        when (event) {
            ArtistEvent.DeleteArtist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    // On supprime d'abord les musiques de l'artiste :
                    for (music in state.value.selectedArtist.musics){
                        musicDao.deleteMusic(music)
                    }
                    // On supprime ensuite tous les albums de l'artiste :
                    val albumsToDelete = albumDao.getAllAlbumsFromArtist(
                        artistId = state.value.selectedArtist.artist.artistId
                    )
                    for (album in albumsToDelete) {
                        albumDao.deleteAlbum(album)
                    }
                    // On supprime enfin l'artiste :
                    artistDao.deleteArtist(state.value.selectedArtist.artist)
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
                SharedPrefUtils.updateIntValue(
                    keyToUpdate = SharedPrefUtils.SORT_ARTISTS_DIRECTION_KEY,
                    newValue = event.type
                )
            }
            is ArtistEvent.SetSortType -> {
                _sortType.value = event.type
                SharedPrefUtils.updateIntValue(
                    keyToUpdate = SharedPrefUtils.SORT_ARTISTS_TYPE_KEY,
                    newValue = event.type
                )
            }
            is ArtistEvent.UpdateQuickAccessState -> {
                CoroutineScope(Dispatchers.IO).launch {
                    artistDao.updateQuickAccessState(
                        newQuickAccessState = !state.value.selectedArtist.artist.isInQuickAccess,
                        artistId = state.value.selectedArtist.artist.artistId
                    )
                }
            }
            else -> {}
        }
    }
}