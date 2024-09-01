package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpdatePlaylistNbPlayedUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
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
) : ScreenModel, PlaylistDetailListener, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
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
                    playlistWithMusics == null -> SelectedPlaylistState.Loading
                    else -> SelectedPlaylistState.Data(
                        playlistDetail = playlistWithMusics.toPlaylistDetail(),
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
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedPlaylistNavigationState.Idle
    }

    /**
     * Set the selected playlist.
     */
    fun init(playlistId: UUID) {
        _playlistId.value = playlistId
    }

    override val onEdit: (() -> Unit)? = {
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
}