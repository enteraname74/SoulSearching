package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetMode
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
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
    destination: SelectedMonthDestination,
) : ViewModel(),
    PlaylistDetailListener,
    MultiMusicBottomSheetDelegate by multiMusicBottomSheetDelegateImpl,
    MultiSelectionManager by multiSelectionManagerImpl {

    val multiSelectionState = multiSelectionManagerImpl.state
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
            searchResult
        ) { monthMusicPreview, duration, searchMusics ->
        when {
            monthMusicPreview == null -> SelectedMonthState.Error
            else -> SelectedMonthState.Data(
                playlistDetail = monthMusicPreview.toPlaylistDetail(
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
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

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedMonthNavigationState> = MutableStateFlow(
        SelectedMonthNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedMonthNavigationState> = _navigationState.asStateFlow()

    init {
        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
            musicBottomSheetMode = MusicBottomSheetMode.ALBUM_OR_ARTIST,
        )
    }

    fun consumeNavigation() {
        multiSelectionManagerImpl.clearMultiSelection()
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
        multiSelectionManagerImpl.clearMultiSelection()
    }

    override fun onMoreClickedOnSelection() {
        handleMultiSelectionBottomSheet()
    }

    private fun handleMultiSelectionBottomSheet() {
        viewModelScope.launch {
            val selectedIds = multiSelectionState.value.selectedIds
            if (selectedIds.size == 1) {
                showMusicBottomSheet(musicId = selectedIds[0])
            } else {
                showMultiMusicBottomSheet()
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = SelectedMonthNavigationState.Back
    }

    override fun onShuffleClicked() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromMonth(month)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.playShuffle(musicList = musics)
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
                    playlistId = null,
                    isMainPlaylist = false
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onSearch(search: String) {
        _searchQuery.value = search
    }

    override fun showMusicBottomSheet(musicId: UUID) {
        _navigationState.value = SelectedMonthNavigationState.ToMusicBottomSheet(musicId)
    }
}