package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetAllAlbumWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.events.AlbumEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AlbumState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllAlbumsNavigationState
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
    private val albumBottomSheetDelegateImpl: AlbumBottomSheetDelegateImpl,
) : ScreenModel, AlbumBottomSheetDelegate by albumBottomSheetDelegateImpl {
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
            else -> {}
        }
    }

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _navigationState: MutableStateFlow<AllAlbumsNavigationState> = MutableStateFlow(
        AllAlbumsNavigationState.Idle
    )
    val navigationState: StateFlow<AllAlbumsNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        _navigationState.value = AllAlbumsNavigationState.Idle
    }

    init {
        albumBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyAlbum = { _navigationState.value = AllAlbumsNavigationState.ToModifyAlbum(it) }
        )
    }
}