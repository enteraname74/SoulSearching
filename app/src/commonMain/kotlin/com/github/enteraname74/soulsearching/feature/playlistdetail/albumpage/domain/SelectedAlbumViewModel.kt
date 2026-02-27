package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumDestination
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedAlbumViewModel(
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    private val multiSelectionManager: MultiSelectionManager,
    settings: SoulSearchingSettings,
    destination: SelectedAlbumDestination,
) :
    ViewModel(),
    PlaylistDetailListener {

    val multiSelectionState = multiSelectionManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )
    private var albumId: UUID = destination.selectedAlbumId

    private val musics: Flow<PagingData<Music>> = commonMusicUseCase
        .getAllPagedOfAlbum(albumId)
        .cachedIn(viewModelScope)

    private var _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResult: Flow<List<Music>> = _searchQuery.flatMapLatest { search ->
        if (search.isNotBlank()) {
            commonMusicUseCase.searchFromAlbum(
                albumId = albumId,
                search = search,
            )
        } else {
            flowOf(emptyList())
        }
    }

    var state: StateFlow<SelectedAlbumState> =
        combine(
            commonAlbumUseCase.getAlbumPreview(albumId = albumId),
            commonMusicUseCase.getAlbumDuration(albumId),
            searchResult,
            settings.getFlowOn(SoulSearchingSettingsKeys.Album.SHOULD_SHOW_TRACK_POSITION_IN_ALBUM_VIEW),
        ) { albumPreview, duration, searchMusics, showTrackPosition ->
            when {
                albumPreview == null -> SelectedAlbumState.Error
                else -> SelectedAlbumState.Data(
                    playlistDetail = albumPreview.toPlaylistDetail(
                        shouldShowTrackPosition = showTrackPosition,
                        musics = musics,
                        duration = duration,
                        searchMusics = searchMusics,
                    ),
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SelectedAlbumState.Loading
        )

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _navigationState: MutableStateFlow<SelectedAlbumNavigationState> = MutableStateFlow(
        SelectedAlbumNavigationState.Idle,
    )
    val navigationState: StateFlow<SelectedAlbumNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        multiSelectionManager.clearMultiSelection()
        _navigationState.value = SelectedAlbumNavigationState.Idle
    }

    override val onEdit: (() -> Unit) = {
        _navigationState.value = SelectedAlbumNavigationState.ToEdit(albumId = albumId)
    }

    override fun onUpdateNbPlayed(musicId: UUID) {
        viewModelScope.launch {
            commonMusicUseCase.incrementNbPlayed(musicId)
        }
    }

    override fun onUpdateNbPlayed() {
        viewModelScope.launch {
            val albumId: UUID =
                (state.value as? SelectedAlbumState.Data)?.playlistDetail?.id ?: return@launch
            commonAlbumUseCase.incrementAlbumNbPlayed(albumId = albumId)
        }
    }

    override fun onSubtitleClicked() {
        viewModelScope.launch {
            val artistId = commonAlbumUseCase
                .getFromId(albumId)
                .firstOrNull()
                ?.artist?.artistId ?: return@launch
            _navigationState.value = SelectedAlbumNavigationState.ToArtist(artistId = artistId)
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

    fun navigateBack() {
        _navigationState.value = SelectedAlbumNavigationState.Back
    }

    override fun onShuffleClicked() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromAlbum(albumId)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.playShuffle(musicList = musics)
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    override fun onPlayClicked(music: Music?) {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getAllMusicFromAlbum(albumId)

            if (musics.isNotEmpty()) {
                onUpdateNbPlayed()
                playbackManager.setCurrentPlaylistAndMusic(
                    music = music ?: musics.first(),
                    musicList = musics,
                    playlistId = albumId,
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
        _navigationState.value = SelectedAlbumNavigationState.ToMusicBottomSheet(musicIds)
    }
}