package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.usecase.album.GetAlbumsOfArtistsUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectedArtistViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val getAlbumsOfArtistsUseCase: GetAlbumsOfArtistsUseCase,
    private val upsertArtistUseCase: UpsertArtistUseCase,
    private val albumBottomSheetDelegateImpl: AlbumBottomSheetDelegateImpl,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
) :
    ScreenModel,
    AlbumBottomSheetDelegate by albumBottomSheetDelegateImpl,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl {

    @OptIn(ExperimentalCoroutinesApi::class)
    var state = getAllPlaylistWithMusicsUseCase().mapLatest { playlists ->
        SelectedArtistState(allPlaylists = playlists)
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedArtistState()
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
            getAllPlaylistsWithMusics = { state.value.allPlaylists },
            setAddToPlaylistBottomSheetState = { _addToPlaylistBottomSheet.value = it }
        )
    }

    fun consumeNavigation() {
        _navigationState.value = SelectedArtistNavigationState.Idle
    }

    /**
     * Handles the events of the selected artist screen.
     */
    fun onEvent(event: SelectedArtistEvent) {
        when (event) {
            is SelectedArtistEvent.AddNbPlayed -> incrementNbPlayed(artistId = event.artistId)
            is SelectedArtistEvent.SetSelectedArtist -> setSelectedArtist(artistId = event.artistId)
        }
    }

    /**
     * Set the selected artist.
     */
    private fun setSelectedArtist(artistId: UUID) {
        state = combine(
            getArtistWithMusicsUseCase(artistId = artistId),
            getAllPlaylistWithMusicsUseCase(),
            getAlbumsOfArtistsUseCase(artistId = artistId),
        ) { artist, playlists, albums ->
            state.value.copy(
                artistWithMusics = artist ?: ArtistWithMusics(),
                allPlaylists = playlists,
                artistAlbums = albums
            )
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            SelectedArtistState()
        )
    }

    /**
     * Increment by one the number of time an artist was played.
     */
    private fun incrementNbPlayed(artistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val artist = state.value.artistWithMusics.artist
            upsertArtistUseCase(
                artist = artist.copy(
                    nbPlayed = artist.nbPlayed + 1
                )
            )
        }
    }
}