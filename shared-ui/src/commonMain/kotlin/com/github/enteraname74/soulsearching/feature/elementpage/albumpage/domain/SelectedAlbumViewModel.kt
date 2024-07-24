package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedAlbumViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase,
    private val upsertAlbumUseCase: UpsertAlbumUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {

    @OptIn(ExperimentalCoroutinesApi::class)
    var state = getAllPlaylistWithMusicsUseCase().mapLatest { playlists ->
        SelectedAlbumState(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedAlbumState()
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedAlbumNavigationState> = MutableStateFlow(
        SelectedAlbumNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedAlbumNavigationState> = _navigationState.asStateFlow()

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = SelectedAlbumNavigationState.ToModifyMusic(it) },
            getAllPlaylistsWithMusics = { state.value.allPlaylists },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.ALBUM_OR_ARTIST,
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedAlbumNavigationState.Idle
    }

    /**
     * Manage album events.
     */
    fun onEvent(event: SelectedAlbumEvent) {
        when (event) {
            is SelectedAlbumEvent.AddNbPlayed -> incrementNbPlayed(albumId = event.albumId)
            is SelectedAlbumEvent.SetSelectedAlbum -> setSelectedAlbum(albumId = event.albumId)
        }
    }

    /**
     * Set the selected album.
     */
    private fun setSelectedAlbum(albumId: UUID) {
        state = combine(
            getAlbumWithMusicsUseCase(albumId = albumId)
                .stateIn(
                    screenModelScope, SharingStarted.WhileSubscribed(), AlbumWithMusics()
                ),
            getAllPlaylistWithMusicsUseCase()
        ) { album, playlists ->
            state.value.copy(
                albumWithMusics = album ?: AlbumWithMusics(),
                allPlaylists = playlists
            )
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedAlbumState()
        )
    }

    /**
     * Increment by one the number of time an album was played.
     */
    private fun incrementNbPlayed(albumId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val album = state.value.albumWithMusics.album
            upsertAlbumUseCase(
                album = album.copy(
                    nbPlayed = album.nbPlayed + 1,
                )
            )
        }
    }
}