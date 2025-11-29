package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.toPlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation.SelectedPlaylistDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedPlaylistViewModel(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
    destination: SelectedPlaylistDestination,
) : ViewModel(),
    PlaylistDetailListener,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl,
    MultiMusicBottomSheetDelegate by multiMusicBottomSheetDelegateImpl,
    MultiSelectionManager by multiSelectionManagerImpl {

    val multiSelectionState = multiSelectionManagerImpl.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    private val allPlaylists: StateFlow<List<PlaylistWithMusics>> = commonPlaylistUseCase.getAllWithMusics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val playlistId: UUID = destination.selectedPlaylistId

    @OptIn(ExperimentalCoroutinesApi::class)
    var state = commonPlaylistUseCase.getWithMusics(playlistId).mapLatest { playlistWithMusics ->
        when {
            playlistWithMusics == null -> SelectedPlaylistState.Error
            else -> SelectedPlaylistState.Data(
                playlistDetail = playlistWithMusics.toPlaylistDetail(),
                selectedPlaylist = playlistWithMusics.playlist,
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

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedPlaylistNavigationState> = MutableStateFlow(
        SelectedPlaylistNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedPlaylistNavigationState> = _navigationState.asStateFlow()

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = SelectedPlaylistNavigationState.ToModifyMusic(it.musicId) },
            getAllPlaylistsWithMusics = { allPlaylists.value },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.PLAYLIST,
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
            musicBottomSheetState = MusicBottomSheetState.PLAYLIST,
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
            val currentPlaylist = (state.value as SelectedPlaylistState.Data).selectedPlaylist
            if (selectedIds.size == 1) {
                val selectedMusic: Music = commonMusicUseCase.getFromId(musicId = selectedIds[0]).firstOrNull() ?: return@launch
                showMusicBottomSheet(
                    selectedMusic = selectedMusic,
                    currentPlaylist = currentPlaylist,
                )
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
}