package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.toPlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.presentation.SelectedFolderDestination
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedFolderViewModel(
    commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
    destination: SelectedFolderDestination,
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

    private val folderPath: String = destination.selectedFolderPath

    private val allPlaylists: StateFlow<List<PlaylistWithMusics>> =
        commonPlaylistUseCase.getAllWithMusics()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    private val musics: Flow<PagingData<Music>> = commonMusicUseCase
        .getAllPagedByNameAscOfFolder(folderPath)
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    var state: StateFlow<SelectedFolderState> =
        combine(
            commonMusicUseCase.getMusicFolderPreview(folder = folderPath),
            commonMusicUseCase.getFolderMusicsDuration(folderPath)
        ) { musicFolderPreview, duration ->
            when {
                musicFolderPreview == null -> SelectedFolderState.Error
                else -> SelectedFolderState.Data(
                    playlistDetail = musicFolderPreview.toPlaylistDetail(
                        musics = musics,
                        duration = duration,
                    )
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SelectedFolderState.Loading
        )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> =
        MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> =
        _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedFolderNavigationState> =
        MutableStateFlow(
            SelectedFolderNavigationState.Idle,
        )
    val navigationState: StateFlow<SelectedFolderNavigationState> = _navigationState.asStateFlow()

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = {
                _navigationState.value = SelectedFolderNavigationState.ToModifyMusic(it.musicId)
            },
            getAllPlaylistsWithMusics = { allPlaylists.value },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )
    }

    fun consumeNavigation() {
        multiSelectionManagerImpl.clearMultiSelection()
        _navigationState.value = SelectedFolderNavigationState.Idle
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
                val selectedMusic: Music =
                    commonMusicUseCase.getFromId(musicId = selectedIds[0]).firstOrNull()
                        ?: return@launch
                showMusicBottomSheet(selectedMusic = selectedMusic)
            } else {
                showMultiMusicBottomSheet()
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = SelectedFolderNavigationState.Back
    }

    override fun onShuffleClicked() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromFolder(folderPath)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.playShuffle(musicList = musics)
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onPlayClicked(music: Music?) {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromFolder(folderPath)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.setCurrentPlaylistAndMusic(
                    music = music ?: musics.first(),
                    musicList = musics,
                    playlistId = null,
                    isMainPlaylist = false
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }
}