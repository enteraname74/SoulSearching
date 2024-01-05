package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.classes.utils.Utils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.states.AlbumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for managing all albums.
 */
@HiltViewModel
class AllAlbumsViewModel @Inject constructor(
    private val albumDao: AlbumDao,
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
    private val musicArtistDao: MusicArtistDao
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _albums = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> albumDao.getAllAlbumsWithMusicsSortByNameAsc()
                        SortType.ADDED_DATE -> albumDao.getAllAlbumsWithMusicsSortByAddedDateAsc()
                        SortType.NB_PLAYED -> albumDao.getAllAlbumsWithMusicsSortByNbPlayedAsc()
                        else -> albumDao.getAllAlbumsWithMusicsSortByNameAsc()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> albumDao.getAllAlbumsWithMusicsSortByNameDesc()
                        SortType.ADDED_DATE -> albumDao.getAllAlbumsWithMusicsSortByAddedDateDesc()
                        SortType.NB_PLAYED -> albumDao.getAllAlbumsWithMusicsSortByNbPlayedDesc()
                        else -> albumDao.getAllAlbumsWithMusicsSortByNameDesc()
                    }
                }
                else -> albumDao.getAllAlbumsWithMusicsSortByNameAsc()
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(AlbumState())
    val state = combine(
        _albums,
        _state,
        _sortDirection,
        _sortType
    ) { albums, state, sortDirection, sortType ->
        state.copy(
            albums = albums.filter { it.musics.any { music -> !music.isHidden } }
                .map { it.toAlbumWithArtist() },
            sortDirection = sortDirection,
            sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AlbumState()
    )

    /**
     * Manage album events.
     */
    fun onAlbumEvent(event: AlbumEvent) {
        when (event) {
            AlbumEvent.DeleteAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {
                    // On supprime d'abord les musiques de l'album :
                    musicDao.deleteMusicFromAlbum(
                        album = state.value.selectedAlbumWithArtist.album.albumName,
                        artist = state.value.selectedAlbumWithArtist.artist!!.artistName
                    )
                    // On supprime ensuite l'album :
                    albumDao.deleteAlbum(state.value.selectedAlbumWithArtist.album)

                    // On vérifie si on peut supprimer l'artiste :
                    Utils.checkAndDeleteArtist(
                        artistToCheck = state.value.selectedAlbumWithArtist.artist!!,
                        musicArtistDao = musicArtistDao,
                        artistDao = artistDao
                    )
                }
            }

            is AlbumEvent.DeleteAlbum ->
                viewModelScope.launch {
                }
            is AlbumEvent.SetSelectedAlbum -> {
                _state.update {
                    it.copy(
                        selectedAlbumWithArtist = event.albumWithArtist
                    )
                }
            }
            is AlbumEvent.BottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            is AlbumEvent.DeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            is AlbumEvent.SetSortType -> {
                _sortType.value = event.type
                SharedPrefUtils.updateIntValue(
                    keyToUpdate = SharedPrefUtils.SORT_ALBUMS_TYPE_KEY,
                    newValue = event.type
                )
            }
            is AlbumEvent.SetSortDirection -> {
                _sortDirection.value = event.type
                SharedPrefUtils.updateIntValue(
                    keyToUpdate = SharedPrefUtils.SORT_ALBUMS_DIRECTION_KEY,
                    newValue = event.type
                )
            }
            is AlbumEvent.UpdateQuickAccessState -> {
                CoroutineScope(Dispatchers.IO).launch {
                    albumDao.updateQuickAccessState(
                        newQuickAccessState = !state.value.selectedAlbumWithArtist.album.isInQuickAccess,
                        albumId = state.value.selectedAlbumWithArtist.album.albumId
                    )
                }
            }
            else -> {}
        }
    }
}