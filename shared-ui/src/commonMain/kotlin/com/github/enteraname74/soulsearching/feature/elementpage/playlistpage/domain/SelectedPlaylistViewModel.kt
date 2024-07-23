package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistNavigationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedPlaylistViewModel(
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
    private val _playlists = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    var state = _playlists.mapLatest { playlists ->
        SelectedPlaylistState(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedPlaylistState()
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
            getAllPlaylistsWithMusics = { state.value.allPlaylists },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.PLAYLIST,
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedPlaylistNavigationState.Idle
    }

    /**
     * Handles events of the selected playlist screen.
     */
    fun onEvent(event: SelectedPlaylistEvent) {
        when(event) {
            is SelectedPlaylistEvent.SetSelectedPlaylist -> setSelectedPlaylist(playlistId = event.playlistId)
            is SelectedPlaylistEvent.AddNbPlayed -> incrementNbPlayed(playlistId = event.playlistId)
        }
    }

    /**
     * Set the selected playlist.
     */
    private fun setSelectedPlaylist(playlistId: UUID) {
        state = combine(
            getPlaylistWithMusicsUseCase(playlistId = playlistId),
            _playlists
        ) { playlist, playlists ->
            state.value.copy(
                playlistWithMusics = playlist,
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedPlaylistState()
        )
    }

    /**
     * Increment by one the number of time a playlist was played.
     */
    private fun incrementNbPlayed(playlistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlist: Playlist = getPlaylistUseCase(playlistId = playlistId).first() ?: return@launch
            upsertPlaylistUseCase(
                playlist = playlist.copy(
                    nbPlayed = playlist.nbPlayed + 1,
                )
            )
        }
    }
}