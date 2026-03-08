package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.toPlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.presentation.SelectedMonthDestination
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedMonthViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    private val multiSelectionManager: MultiSelectionManager,
    destination: SelectedMonthDestination,
) : ViewModel(), PlaylistDetailListener {

    val multiSelectionState = multiSelectionManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    private val month: String = destination.month

    private val musics: Flow<PagingData<Music>> = commonMusicUseCase
        .getAllPagedByNameAscOfMonth(month)
        .cachedIn(viewModelScope)

    private var _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResult: Flow<List<Music>> = _searchQuery.flatMapLatest { search ->
        if (search.isNotBlank()) {
            commonMusicUseCase.searchFromMonth(
                month = month,
                search = search,
            )
        } else {
            flowOf(emptyList())
        }
    }

    var state =
        combine(
            commonMusicUseCase.getMonthMusicPreview(month = month),
            commonMusicUseCase.getMonthMusicsDuration(month),
            searchResult,
            playbackManager.getCachedPlaylist(month),
        ) { monthMusicPreview, duration, searchMusics, cachedPlaylist ->
        when {
            monthMusicPreview == null -> SelectedMonthState.Error
            else -> SelectedMonthState.Data(
                playlistDetail = monthMusicPreview.toPlaylistDetail(
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                    cachedPlaylist = cachedPlaylist,
                ),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SelectedMonthState.Loading,
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedMonthNavigationState> = MutableStateFlow(
        SelectedMonthNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedMonthNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        multiSelectionManager.clearMultiSelection()
        _navigationState.value = SelectedMonthNavigationState.Idle
    }

    override val onEdit: (() -> Unit)? = null

    override fun onUpdateNbPlayed(musicId: UUID) {
        viewModelScope.launch {
            commonMusicUseCase.incrementNbPlayed(musicId = musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        /* no-op */
    }

    override fun onCloseSelection() {
        multiSelectionManager.clearMultiSelection()
    }

    override fun onLongClickOnMusic(musicId: UUID) {
        multiSelectionManager.toggleElementInSelection(
            id = musicId,
            mode = SelectionMode.Music,
        )
    }

    fun navigateBack() {
        _navigationState.value = SelectedMonthNavigationState.Back
    }

    override fun onShuffleClicked() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromMonth(month)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.playShuffle(
                    musicList = musics,
                    playlistId = month,
                    isMain = false,
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onPlayClicked(music: Music?) {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromMonth(month)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.setCurrentPlaylistAndMusic(
                    music = music ?:  musics.first(),
                    musicList = musics,
                    playlistId = month,
                    isMainPlaylist = false
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onSearch(search: String) {
        _searchQuery.value = search
    }

    override fun showMusicBottomSheet(musicIds: List<UUID>) {
        _navigationState.value = SelectedMonthNavigationState.ToMusicBottomSheet(musicIds)
    }

    override fun continuePlayedList(playedListId: UUID) {
        viewModelScope.launch {
            playbackManager.continuePlayedList(playedListId)
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    override fun deletePlayedList(playedListId: UUID) {
        viewModelScope.launch {
            playbackManager.deletePlayedList(playedListId)
        }
    }
}