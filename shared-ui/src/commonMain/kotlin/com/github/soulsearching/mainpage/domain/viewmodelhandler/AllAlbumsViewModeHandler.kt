package com.github.soulsearching.mainpage.domain.viewmodelhandler

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.events.AlbumEvent
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.state.AlbumState
import com.github.soulsearching.mainpage.domain.model.SortDirection
import com.github.soulsearching.mainpage.domain.model.SortType
import com.github.soulsearching.domain.utils.Utils
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
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
 * Handler for managing the AllAlbumsViewModel.
 */
open class AllAlbumsViewModeHandler(
    coroutineScope: CoroutineScope,
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val settings: SoulSearchingSettings
) : ViewModelHandler {
    private val _sortType = MutableStateFlow(SortType.NAME)
    private val _sortDirection = MutableStateFlow(SortDirection.ASC)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _albums = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> albumRepository.getAllAlbumsWithMusicsSortByNameAscAsFlow()
                        SortType.ADDED_DATE -> albumRepository.getAllAlbumsWithMusicsSortByAddedDateAscAsFlow()
                        SortType.NB_PLAYED -> albumRepository.getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow()
                        else -> albumRepository.getAllAlbumsWithMusicsSortByNameAscAsFlow()
                    }
                }
                SortDirection.DESC -> {
                    when (sortType) {
                        SortType.NAME -> albumRepository.getAllAlbumsWithMusicsSortByNameDescAsFlow()
                        SortType.ADDED_DATE -> albumRepository.getAllAlbumsWithMusicsSortByAddedDateDescAsFlow()
                        SortType.NB_PLAYED -> albumRepository.getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow()
                        else -> albumRepository.getAllAlbumsWithMusicsSortByNameDescAsFlow()
                    }
                }
                else -> albumRepository.getAllAlbumsWithMusicsSortByNameAscAsFlow()
            }
        }
    }.stateIn(
        coroutineScope,
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
        coroutineScope,
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
                    musicRepository.deleteMusicFromAlbum(
                        album = state.value.selectedAlbumWithArtist.album.albumName,
                        artist = state.value.selectedAlbumWithArtist.artist!!.artistName
                    )
                    // On supprime ensuite l'album :
                    albumRepository.deleteAlbum(state.value.selectedAlbumWithArtist.album)

                    // On vérifie si on peut supprimer l'artiste :
                    Utils.checkAndDeleteArtist(
                        artistToCheck = state.value.selectedAlbumWithArtist.artist!!,
                        musicArtistRepository = musicArtistRepository,
                        artistRepository = artistRepository
                    )
                }
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
                settings.setInt(
                    key = SoulSearchingSettings.SORT_ALBUMS_TYPE_KEY,
                    value = event.type
                )
            }
            is AlbumEvent.SetSortDirection -> {
                _sortDirection.value = event.type
                settings.setInt(
                    key = SoulSearchingSettings.SORT_ALBUMS_DIRECTION_KEY,
                    value = event.type
                )
            }
            is AlbumEvent.UpdateQuickAccessState -> {
                CoroutineScope(Dispatchers.IO).launch {
                    albumRepository.updateQuickAccessState(
                        newQuickAccessState = !state.value.selectedAlbumWithArtist.album.isInQuickAccess,
                        albumId = state.value.selectedAlbumWithArtist.album.albumId
                    )
                }
            }
            else -> {}
        }
    }
}