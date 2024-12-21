package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
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
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.toPlaylistDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedFolderViewModel(
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val getMusicFolderListUseCase: GetMusicFolderListUseCase,
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

    private val _folderPath: MutableStateFlow<String?> = MutableStateFlow(null)

    private val allPlaylists: StateFlow<List<PlaylistWithMusics>> = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    var state: StateFlow<SelectedFolderState> = _folderPath.flatMapLatest { folderPath ->
        if (folderPath == null) {
            flowOf(SelectedFolderState.Loading)
        } else {
            getMusicFolderListUseCase(path = folderPath).mapLatest { musicFolderList ->
                when {
                    musicFolderList == null -> SelectedFolderState.Loading
                    else -> SelectedFolderState.Data(
                        playlistDetail = musicFolderList.toPlaylistDetail()
                    )
                }
            }
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SelectedFolderState.Loading
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedFolderNavigationState> = MutableStateFlow(
        SelectedFolderNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedFolderNavigationState> = _navigationState.asStateFlow()

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = SelectedFolderNavigationState.ToModifyMusic(it) },
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


    /**
     * Set the selected playlist.
     */
    fun init(folderPath: String) {
        _folderPath.value = folderPath
    }

    override val onEdit: (() -> Unit)? = null

    override fun onUpdateNbPlayed(musicId: UUID) {
        screenModelScope.launch {
            updateMusicNbPlayedUseCase(musicId = musicId)
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
        screenModelScope.launch {
            val selectedIds = multiSelectionState.value.selectedIds
            if (selectedIds.size == 1) {
                val selectedMusic: Music = getMusicUseCase(musicId = selectedIds[0]).firstOrNull() ?: return@launch
                showMusicBottomSheet(selectedMusic = selectedMusic)
            } else {
                showMultiMusicBottomSheet()
            }
        }
    }
}