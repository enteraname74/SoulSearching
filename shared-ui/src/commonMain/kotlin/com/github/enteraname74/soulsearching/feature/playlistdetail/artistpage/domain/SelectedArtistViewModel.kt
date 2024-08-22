package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.album.GetAlbumsOfArtistsUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistNbPlayedUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicNbPlayedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegateImpl
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

class SelectedArtistViewModel(
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val getAlbumsOfArtistsUseCase: GetAlbumsOfArtistsUseCase,
    private val updateArtistNbPlayedUseCase: UpdateArtistNbPlayedUseCase,
    private val updateMusicNbPlayedUseCase: UpdateMusicNbPlayedUseCase,
    private val albumBottomSheetDelegateImpl: AlbumBottomSheetDelegateImpl,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) :
    ScreenModel,
    PlaylistDetailListener,
    AlbumBottomSheetDelegate by albumBottomSheetDelegateImpl,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {
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
                getAlbumsOfArtistsUseCase(artistId = artistId),
                getArtistWithMusicsUseCase(artistId = artistId),
            ) { albums, artistWithMusics ->
                when {
                    artistWithMusics == null -> SelectedArtistState.Loading
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
            onModifyAlbum = { _navigationState.value = SelectedArtistNavigationState.ToModifyAlbum(it) }
        )

        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyMusic = { _navigationState.value = SelectedArtistNavigationState.ToModifyMusic(it) },
            getAllPlaylistsWithMusics = { allPlaylists.value },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it },
            musicBottomSheetState = MusicBottomSheetState.ALBUM_OR_ARTIST,
        )
    }

    fun consumeNavigation() {
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

    override fun onEdit() {
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
}