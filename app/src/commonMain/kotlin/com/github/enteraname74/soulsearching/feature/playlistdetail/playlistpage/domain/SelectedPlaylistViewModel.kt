package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegateImpl
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
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation.SelectedPlaylistDestination
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedPlaylistViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
    destination: SelectedPlaylistDestination,
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

    private val playlistId: UUID = destination.selectedPlaylistId

    private val musics: Flow<PagingData<Music>> = commonMusicUseCase
        .getAllPagedByNameAscOfPlaylist(playlistId)
        .cachedIn(viewModelScope)

    private var _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResult: Flow<List<Music>> = _searchQuery.flatMapLatest { search ->
        if (search.isNotBlank()) {
            commonMusicUseCase.searchFromPlaylist(
                playlistId = playlistId,
                search = search,
            )
        } else {
            flowOf(emptyList())
        }
    }

    var state = combine(
        commonMusicUseCase.getPlaylistDuration(playlistId),
        commonPlaylistUseCase.getPlaylistPreview(playlistId),
        searchResult
    ) { duration, playlistPreview, searchMusics ->
        when {
            playlistPreview == null -> SelectedPlaylistState.Error
            else -> SelectedPlaylistState.Data(
                playlistDetail = playlistPreview.toPlaylistDetail(
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                ),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SelectedPlaylistState.Loading
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedPlaylistNavigationState> =
        MutableStateFlow(
            SelectedPlaylistNavigationState.Idle,
        )
    val navigationState: StateFlow<SelectedPlaylistNavigationState> = _navigationState.asStateFlow()

    init {
        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
            musicBottomSheetMode = MusicBottomSheetMode.PLAYLIST,
        )
    }

    fun consumeNavigation() {
        multiSelectionManagerImpl.clearMultiSelection()
        _navigationState.value = SelectedPlaylistNavigationState.Idle
    }

    override val onEdit: (() -> Unit) = {
        _navigationState.value = SelectedPlaylistNavigationState.ToEdit(playlistId = playlistId)
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        viewModelScope.launch {
            commonMusicUseCase.incrementNbPlayed(musicId = musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        viewModelScope.launch {
            val playlistId: UUID = (state.value as? SelectedPlaylistState.Data)?.playlistDetail?.id
                ?: return@launch
            commonPlaylistUseCase.incrementNbPlayed(playlistId = playlistId)
        }
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
            val currentPlaylist = commonPlaylistUseCase
                .getFromId(playlistId).firstOrNull() ?: return@launch

            if (selectedIds.size == 1) {
                showMusicBottomSheet(selectedIds[0])
            } else {
                showMultiMusicBottomSheet(
                    currentPlaylist = currentPlaylist,
                )
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = SelectedPlaylistNavigationState.Back
    }

    override fun onShuffleClicked() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromPlaylist(playlistId)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.playShuffle(musicList = musics)
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onPlayClicked(music: Music?) {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromPlaylist(playlistId)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.setCurrentPlaylistAndMusic(
                    music = music ?: musics.first(),
                    musicList = musics,
                    playlistId = playlistId,
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
        _navigationState.value = SelectedPlaylistNavigationState.ToMusicBottomSheet(musicId)
    }
}