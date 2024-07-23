package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.MusicFolder
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class SelectedFolderViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
    private var _selectedFolder: StateFlow<MusicFolder?> = MutableStateFlow(
        MusicFolder()
    )

    private val _state = MutableStateFlow(SelectedFolderState())
    var state = combine(
        _state,
        getAllPlaylistWithMusicsUseCase()
    ) { state, playlists ->
        state.copy(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedFolderState()
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
            getAllPlaylistsWithMusics = { state.value.allPlaylists },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedFolderNavigationState.Idle
    }

    /**
     * Handles events of the selected folder screen.
     */
    fun onEvent(event: SelectedFolderEvent) {
        when (event) {
            is SelectedFolderEvent.SetSelectedFolder -> setSelectedFolder(path = event.folderPath)
            is SelectedFolderEvent.AddNbPlayed -> incrementNbPlayed(playlistId = event.playlistId)
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedFolder(path: String) {
        _selectedFolder = getAllMusicUseCase()
            .map { allMusics ->
                val musics = allMusics.filter { it.folder == path }
                MusicFolder(
                    path = path,
                    musics = musics,
                    coverId = musics.firstOrNull { it.coverId != null }?.coverId
                )
            }
            .stateIn(
                screenModelScope, SharingStarted.WhileSubscribed(), MusicFolder()
            )

        state = combine(
            _state,
            _selectedFolder,
            getAllPlaylistWithMusicsUseCase()
        ) { state, folder, playlists ->
            state.copy(
                musicFolder = folder,
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedFolderState()
        )

        _state.update {
            it.copy(
                musicFolder = _selectedFolder.value
            )
        }
    }

    /**
     * Increment by one the number of time a playlist was played.
     */
    private fun incrementNbPlayed(playlistId: UUID) {
    }
}