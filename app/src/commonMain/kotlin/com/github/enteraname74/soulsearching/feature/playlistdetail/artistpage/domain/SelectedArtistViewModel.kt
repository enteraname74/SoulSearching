package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.toPlaylistDetail
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedArtistViewModel(
    commonAlbumUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    private val multiSelectionManager: MultiSelectionManager,
    destination: SelectedArtistDestination,
) : ViewModel(), PlaylistDetailListener {

    val multiSelectionState = multiSelectionManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    private val artistId: UUID = destination.selectedArtistId

    private val musics: Flow<PagingData<Music>> = commonMusicUseCase
        .getAllPagedByNameAscOfArtist(artistId)
        .cachedIn(viewModelScope)

    private var _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResult: Flow<List<Music>> = _searchQuery.flatMapLatest { search ->
        if (search.isNotBlank()) {
            commonMusicUseCase.searchFromArtist(
                artistId = artistId,
                search = search,
            )
        } else {
            flowOf(emptyList())
        }
    }

    var state: StateFlow<SelectedArtistState> = combine(
        commonAlbumUseCase.getAlbumsWithMusicsOfArtist(artistId = artistId),
        commonArtistUseCase.getArtistPreview(artistId = artistId),
        commonMusicUseCase.getArtistDuration(artistId),
        searchResult
    ) { albums, artistPreview, duration, searchMusics ->
        when {
            artistPreview == null -> SelectedArtistState.Error
            else -> SelectedArtistState.Data(
                playlistDetail = artistPreview.toPlaylistDetail(
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                ),
                artistAlbums = albums,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SelectedArtistState.Loading
    )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedArtistNavigationState> = MutableStateFlow(
        SelectedArtistNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedArtistNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        multiSelectionManager.clearMultiSelection()
        _navigationState.value = SelectedArtistNavigationState.Idle
    }

    fun toAlbum(albumId: UUID) {
        _navigationState.value = SelectedArtistNavigationState.ToAlbum(albumId = albumId)
    }

    override val onEdit: (() -> Unit) = {
        _navigationState.value = SelectedArtistNavigationState.ToEdit(artistId = artistId)
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        viewModelScope.launch {
            commonMusicUseCase.incrementNbPlayed(musicId = musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        viewModelScope.launch {
            val artistId: UUID = (state.value as? SelectedArtistState.Data)?.playlistDetail?.id ?: return@launch
            commonArtistUseCase.incrementArtistNbPlayed(artistId = artistId)
        }
    }

    override fun onCloseSelection() {
        multiSelectionManager.clearMultiSelection()
    }

    override fun onLongClickOnMusic(musicId: UUID) {
        multiSelectionManager.toggleElementInSelection(
            id = musicId,
            mode = SelectionMode.Music,
        )
    }

    fun onLongClickOnAlbum(albumId: UUID) {
        multiSelectionManager.toggleElementInSelection(
            id = albumId,
            mode = SelectionMode.Album,
        )
    }

    private fun handleMultiSelectionAlbumBottomSheet() {
        val selectedIds = multiSelectionState.value.selectedIds
        if (selectedIds.size == 1) {
            val selectedAlbum: AlbumWithMusics =
                (state.value as? SelectedArtistState.Data)?.artistAlbums?.find {
                    it.album.albumId ==  selectedIds[0]
                } ?: return
//            showAlbumBottomSheet(albumWithMusics = selectedAlbum)
        } else {
//            showMultiAlbumBottomSheet()
        }
    }

    private fun handleMultiSelectionBottomSheet() {
        viewModelScope.launch {

            if (multiSelectionManager.selectionMode == SelectionMode.Music) {
                showMusicBottomSheet(multiSelectionState.value.selectedIds)
            } else if (multiSelectionManager.selectionMode == SelectionMode.Album) {
                handleMultiSelectionAlbumBottomSheet()
            }
        }
    }

    fun navigateBack() {
        _navigationState.value = SelectedArtistNavigationState.Back
    }

    override fun onShuffleClicked() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromArtist(artistId)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.playShuffle(musicList = musics)
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onPlayClicked(music: Music?) {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromArtist(artistId)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.setCurrentPlaylistAndMusic(
                    music = music ?: musics.first(),
                    musicList = musics,
                    playlistId = artistId,
                    isMainPlaylist = false
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onSearch(search: String) {
        _searchQuery.value = search
    }

    override fun showMusicBottomSheet(musicIds: List<UUID>) {
        _navigationState.value = SelectedArtistNavigationState.ToMusicBottomSheet(musicIds)
    }
}