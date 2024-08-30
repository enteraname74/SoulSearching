package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumNbPlayedUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
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

class SelectedAlbumViewModel(
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase,
    private val updateAlbumNbPlayedUseCase: UpdateAlbumNbPlayedUseCase,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) : ScreenModel, MusicBottomSheetDelegate by musicBottomSheetDelegateImpl, PlaylistDetailListener{
    private var _albumId: MutableStateFlow<UUID?> = MutableStateFlow(null)

    private val allPlaylists: StateFlow<List<PlaylistWithMusics>> = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    var state: StateFlow<SelectedAlbumState> = _albumId.flatMapLatest { albumId ->
        if (albumId == null) {
            flowOf(SelectedAlbumState.Loading)
        } else {
            getAlbumWithMusicsUseCase(albumId = albumId).mapLatest {albumWithMusics ->
                when {
                    albumWithMusics == null -> SelectedAlbumState.Loading
                    else -> SelectedAlbumState.Data(
                        playlistDetail = albumWithMusics.toPlaylistDetail(),
                        artistId = albumWithMusics.artist?.artistId,
                    )
                }
            }
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SelectedAlbumState.Loading
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
            getAllPlaylistsWithMusics = { allPlaylists.value },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.ALBUM_OR_ARTIST,
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedAlbumNavigationState.Idle
    }

    /**
     * Set the selected album.
     */
    fun init(albumId: UUID) {
        _albumId.value = albumId
    }

    override val onEdit: (() -> Unit) = {
        _navigationState.value = SelectedAlbumNavigationState.ToEdit
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        screenModelScope.launch {
            updateMusicNbPlayedUseCase(musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        screenModelScope.launch {
            println("THERE")
            val albumId: UUID = (state.value as? SelectedAlbumState.Data)?.playlistDetail?.id ?: return@launch
            updateAlbumNbPlayedUseCase(albumId = albumId)
        }
    }

    override fun onSubtitleClicked() {
        val artistId: UUID = (state.value as? SelectedAlbumState.Data)?.artistId ?: return
        _navigationState.value = SelectedAlbumNavigationState.ToArtist(artistId = artistId)
    }
}