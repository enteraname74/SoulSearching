package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodelhandler

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.domain.events.AlbumEvent
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortDirection
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortType
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AlbumState
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
    private val settings: SoulSearchingSettings,
) : ViewModelHandler {
    private val _sortType = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_ALBUMS_TYPE_KEY, SortType.NAME
        )
    )
    private val _sortDirection = MutableStateFlow(
        settings.getInt(
            SoulSearchingSettings.SORT_ALBUMS_DIRECTION_KEY, SortDirection.ASC
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _albums = _sortDirection.flatMapLatest { sortDirection ->
        _sortType.flatMapLatest { sortType ->
            when (sortDirection) {
                SortDirection.ASC -> {
                    when (sortType) {
                        SortType.NAME -> albumRepository.getAllAlbumWithMusics()
                        SortType.ADDED_DATE -> albumRepository.getAllAlbumsWithMusicsSortByAddedDateAscAsFlow()
                        SortType.NB_PLAYED -> albumRepository.getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow()
                        else -> albumRepository.getAllAlbumWithMusics()
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
                else -> albumRepository.getAllAlbumWithMusics()
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
            is AlbumEvent.DeleteAlbum -> {
                CoroutineScope(Dispatchers.IO).launch {
                    albumRepository.delete(albumId = event.albumId)
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
                        newQuickAccessState = !event.album.isInQuickAccess,
                        albumId = event.album.albumId
                    )
                }
            }
            else -> {}
        }
    }
}