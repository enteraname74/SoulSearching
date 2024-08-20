package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.soulsearching.commondelegate.ArtistBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.ArtistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllArtistsNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ArtistState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class AllArtistsViewModel(
    private val settings: SoulSearchingSettings,
    private val getAllArtistWithMusicsSortedUseCase: GetAllArtistWithMusicsSortedUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val upsertArtistUseCase: UpsertArtistUseCase,
    private val artistBottomSheetDelegateImpl: ArtistBottomSheetDelegateImpl,
): ScreenModel, ArtistBottomSheetDelegate by artistBottomSheetDelegateImpl {
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
            else -> {}
        }
    }

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _navigationState: MutableStateFlow<AllArtistsNavigationState> = MutableStateFlow(
        AllArtistsNavigationState.Idle
    )
    val navigationState: StateFlow<AllArtistsNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        _navigationState.value = AllArtistsNavigationState.Idle
    }

    init {
        artistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyArtist = { _navigationState.value = AllArtistsNavigationState.ToModifyArtist(it) }
        )
    }
}