package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetAllAlbumWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.soulsearching.domain.events.AlbumEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AlbumState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AllAlbumsViewModel(
    private val settings: SoulSearchingSettings,
    private val getAllAlbumWithMusicsSortedUseCase: GetAllAlbumWithMusicsSortedUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val upsertAlbumUseCase: UpsertAlbumUseCase,
) : ScreenModel {
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
            getAllAlbumWithMusicsSortedUseCase(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }
    }.stateIn(
        screenModelScope,
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
        screenModelScope,
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
                    deleteAlbumUseCase(albumId = event.albumId)
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
                    upsertAlbumUseCase(
                        album = event.album.copy(
                            isInQuickAccess = !event.album.isInQuickAccess
                        )
                    )
                }
            }
            else -> {}
        }
    }
}