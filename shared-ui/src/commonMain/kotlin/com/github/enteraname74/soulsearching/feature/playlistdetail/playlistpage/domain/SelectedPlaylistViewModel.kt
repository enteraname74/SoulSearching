package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpdatePlaylistNbPlayedUseCase
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedPlaylistViewModel(
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val updatePlaylistNbPlayedUseCase: UpdatePlaylistNbPlayedUseCase,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    private val getMusicUseCase: GetMusicUseCase,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
) : ScreenModel,
    PlaylistDetailListener,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl,
    MultiMusicBottomSheetDelegate by multiMusicBottomSheetDelegateImpl,
    MultiSelectionManager by multiSelectionManagerImpl {

    val multiSelectionState = multiSelectionManagerImpl.state
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    private val allPlaylists = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _playlistId: MutableStateFlow<UUID?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    var state = _playlistId.flatMapLatest { playlistId ->
        if (playlistId == null) {
            flowOf(SelectedPlaylistState.Loading)
        } else {
            getPlaylistWithMusicsUseCase(playlistId).mapLatest { playlistWithMusics ->
                when {
                    playlistWithMusics == null -> SelectedPlaylistState.Error
                    else -> SelectedPlaylistState.Data(
                        playlistDetail = playlistWithMusics.toPlaylistDetail(),
                        selectedPlaylist = playlistWithMusics.playlist,
                    )
                }
            }.stateIn(
                scope = screenModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SelectedPlaylistState.Loading
            )
        }
    }.stateIn(
        scope = screenModelScope,
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
            onModifyMusic = { _navigationState.value = SelectedPlaylistNavigationState.ToModifyMusic(it) },
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

    /**
     * Set the selected playlist.
     */
    fun init(playlistId: UUID) {
        _playlistId.value = playlistId
    }

    override val onEdit: (() -> Unit) = {
        _navigationState.value = SelectedPlaylistNavigationState.ToEdit
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        screenModelScope.launch {
            updateMusicNbPlayedUseCase(musicId = musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        screenModelScope.launch {
            val playlistId: UUID = (state.value as? SelectedPlaylistState.Data)?.playlistDetail?.id
                ?: return@launch
            updatePlaylistNbPlayedUseCase(playlistId = playlistId)
        }
    }

    override fun onCloseSelection() {
        multiSelectionManagerImpl.clearMultiSelection()
    }

    override fun onMoreClickedOnSelection() {
        handleMultiSelectionBottomSheet()
    }

    private fun handleMultiSelectionBottomSheet() {
        screenModelScope.launch {
            val selectedIds = multiSelectionState.value.selectedIds
            val currentPlaylist = (state.value as SelectedPlaylistState.Data).selectedPlaylist
            if (selectedIds.size == 1) {
                val selectedMusic: Music = getMusicUseCase(musicId = selectedIds[0]).firstOrNull() ?: return@launch
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
}