package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.album.GetAlbumsWithMusicsOfArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistNbPlayedUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.*
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

class SelectedArtistViewModel(
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val getAlbumsWithMusicsOfArtistUseCase: GetAlbumsWithMusicsOfArtistUseCase,
    private val updateArtistNbPlayedUseCase: UpdateArtistNbPlayedUseCase,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
    private val albumBottomSheetDelegateImpl: AlbumBottomSheetDelegateImpl,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl,
    private val getMusicUseCase: GetMusicUseCase,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
) :
    ScreenModel,
    PlaylistDetailListener,
    AlbumBottomSheetDelegate by albumBottomSheetDelegateImpl,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl,
    MultiMusicBottomSheetDelegate by multiMusicBottomSheetDelegateImpl,
    MultiSelectionManager by multiSelectionManagerImpl {

    val multiSelectionState = multiSelectionManagerImpl.state
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    private val _artistId: MutableStateFlow<UUID?> = MutableStateFlow(null)

    private val allPlaylists: StateFlow<List<PlaylistWithMusics>> = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    var state: StateFlow<SelectedArtistState> = _artistId.flatMapLatest { artistId ->
        if (artistId == null) {
            flowOf(SelectedArtistState.Loading)
        } else {
            combine(
                getAlbumsWithMusicsOfArtistUseCase(artistId = artistId),
                getArtistWithMusicsUseCase(artistId = artistId),
            ) { albums, artistWithMusics ->
                when {
                    artistWithMusics == null -> SelectedArtistState.Error
                    else -> SelectedArtistState.Data(
                        playlistDetail = artistWithMusics.toPlaylistDetail(),
                        artistAlbums = albums,
                    )
                }
            }
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SelectedArtistState.Loading
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistBottomSheet: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistBottomSheet: StateFlow<AddToPlaylistBottomSheet?> = _addToPlaylistBottomSheet.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedArtistNavigationState> = MutableStateFlow(
        SelectedArtistNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedArtistNavigationState> = _navigationState.asStateFlow()

    init {
        albumBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyAlbum = { _navigationState.value = SelectedArtistNavigationState.ToModifyAlbum(it) },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = SelectedArtistNavigationState.ToModifyMusic(it) },
            getAllPlaylistsWithMusics = { allPlaylists.value },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.ALBUM_OR_ARTIST,
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
            musicBottomSheetState = MusicBottomSheetState.ALBUM_OR_ARTIST,
        )
    }

    fun consumeNavigation() {
        multiSelectionManagerImpl.clearMultiSelection()
        _navigationState.value = SelectedArtistNavigationState.Idle
    }

    /**
     * Set the selected artist.
     */
    fun init(artistId: UUID) {
        _artistId.value = artistId
    }

    fun toAlbum(albumId: UUID) {
        _navigationState.value = SelectedArtistNavigationState.ToAlbum(albumId = albumId)
    }

    override val onEdit: (() -> Unit) = {
        _navigationState.value = SelectedArtistNavigationState.ToEdit
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        screenModelScope.launch {
            updateMusicNbPlayedUseCase(musicId = musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        screenModelScope.launch {
            val artistId: UUID = (state.value as? SelectedArtistState.Data)?.playlistDetail?.id ?: return@launch
            updateArtistNbPlayedUseCase(artistId = artistId)
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
            if (selectedIds.size == 1) {
                val selectedMusic: Music = getMusicUseCase(musicId = selectedIds[0]).firstOrNull() ?: return@launch
                showMusicBottomSheet(selectedMusic = selectedMusic)
            } else {
                showMultiMusicBottomSheet()
            }
        }
    }
}