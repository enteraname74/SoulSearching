package com.github.enteraname74.soulsearching.feature.musiclistdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicListDetailId
import com.github.enteraname74.domain.model.player.PlayedListToContinue
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.MusicItemLeadingSpec
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.CachedPlayedListUiSpec
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.PlaylistSearchViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.PlaylistDetailCover
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class MusicListDetailViewHolder(
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    private val multiSelectionManager: MultiSelectionManager,
    val playlistSearchViewManager: PlaylistSearchViewManager,
    private val settings: SoulSearchingSettings,
    private val detailId: MusicListDetailId,
    private val colorThemeManager: ColorThemeManager,
) : SoulViewModelHolderV2<MusicListDetailNavScope, MusicListDetailState>() {
    val multiSelectionState: StateFlow<MultiSelectionState> = multiSelectionManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MultiSelectionState(emptyList()),
        )

    private val musics: Flow<PagingData<Music>> = commonMusicUseCase.observeMusicsFromMusicListDetailId(
        musicListDetailId = detailId,
    ).cachedIn(viewModelScope)

    private var _searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResult: Flow<List<Music>> = _searchQuery.flatMapLatest { search ->
        if (search.isNotBlank()) {
            commonMusicUseCase.searchFromMusicListDetailId(
                musicListDetailId = detailId,
                search = search,
            )
        } else {
            flowOf(emptyList())
        }
    }

    init {
        viewModelScope.launch {
            when (detailId) {
                is MusicListDetailId.Album -> albumFlow(detailId)
                is MusicListDetailId.Artist -> artistFlow(detailId)
                is MusicListDetailId.Folder -> folderFlow(detailId)
                is MusicListDetailId.Month -> monthFlow(detailId)
                is MusicListDetailId.Playlist -> playlistFlow(detailId)
            }.collectLatest { state ->
                updateState { state }
            }
        }
    }

    override fun getInitialState(): MusicListDetailState = MusicListDetailState.Loading(
        navigateBack = { navigate { navigateBack() } },
    )

    @Composable
    override fun Content(
        state: MusicListDetailState,
    ) {
        MusicListDetailScreen(
            state = state,
            multiSelectionStateFlow = multiSelectionState,
            playlistSearchViewManager = playlistSearchViewManager,
        )
    }

    private fun albumFlow(
        detailId: MusicListDetailId.Album,
    ): Flow<MusicListDetailState> =
        combine(
            commonAlbumUseCase.getAlbumPreview(albumId = detailId.albumId),
            commonMusicUseCase.getAlbumDuration(detailId.albumId),
            searchResult,
            settings.getFlowOn(SoulSearchingSettingsKeys.Album.SHOULD_SHOW_TRACK_POSITION_IN_ALBUM_VIEW),
            playbackManager.getCachedPlaylist(detailId.albumId.toString()),
        ) { albumPreview, duration, searchMusics, showTrackPosition, cachedPlaylist ->
            when {
                albumPreview == null -> MusicListDetailState.Error(
                    error = strings.albumDoesNotExists,
                    navigateBack = { navigate { navigateBack() } },
                )
                else -> MusicListDetailState.Data(
                    cachedPlayedListUiSpec = cachedPlaylist?.toUiSpec(),
                    type = strings.albumDetailTitle,
                    title = albumPreview.name,
                    subTitle = albumPreview.artist,
                    cover = albumPreview.cover,
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                    optionalContent = null,
                    musicItemLeadingSpec = { musicPosition ->
                        if (showTrackPosition) {
                            MusicItemLeadingSpec.Position(pos = musicPosition + 1)
                        } else {
                            MusicItemLeadingSpec.Cover
                        }
                    },
                    navigateBack = { navigate { navigateBack() } },
                    onSubtitleClicked = {
                        viewModelScope.launch {
                            val artistId = commonAlbumUseCase
                                .getFromId(detailId.albumId)
                                .firstOrNull()
                                ?.artist?.artistId ?: return@launch

                            navigate { toDestination(MusicListDetailDestination(MusicListDetailId.Artist(artistId))) }
                        }
                    },
                    onCloseSelection = ::onCloseSelection,
                    onLongClickOnMusic = ::onLongClickOnMusic,
                    onSearch = ::onSearch,
                    showMusicBottomSheet = ::showMusicBottomSheet,
                    onPlay = ::onPlay,
                    onShuffle = ::onShuffle,
                    onEdit = {
                        navigate {
                            toDestination(ModifyAlbumDestination(detailId.albumId))
                        }
                    },
                    onCoverLoaded = ::onCoverLoaded,
                )
            }
        }

    private fun artistFlow(
        detailId: MusicListDetailId.Artist,
    ): Flow<MusicListDetailState> =
        combine(
            commonAlbumUseCase.getAlbumsWithMusicsOfArtist(artistId = detailId.artistId),
            commonArtistUseCase.getArtistPreview(artistId = detailId.artistId),
            commonMusicUseCase.getArtistDuration(detailId.artistId),
            searchResult,
            playbackManager.getCachedPlaylist(detailId.artistId.toString()),
        ) { albums, artistPreview, duration, searchMusics, cachedPlaylist ->
            when {
                artistPreview == null -> MusicListDetailState.Error(
                    error = strings.artistDoesNotExists,
                    navigateBack = { navigate { navigateBack() } },
                )
                else -> MusicListDetailState.Data(
                    cachedPlayedListUiSpec = cachedPlaylist?.toUiSpec(),
                    type = strings.artistDetailTitle,
                    title = artistPreview.name,
                    subTitle = strings.musics(artistPreview.totalMusics),
                    cover = artistPreview.cover,
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                    musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
                    optionalContent = albums.takeIf { it.isNotEmpty() }?.let {
                        MusicListDetailState.Data.OptionalContent.Albums(
                            albums = albums,
                            onClick = {
                                navigate { toDestination(MusicListDetailDestination(MusicListDetailId.Album(it))) }
                            },
                            onLongClick = {
                                multiSelectionManager.toggleElementInSelection(
                                    id = it.toString(),
                                    mode = SelectionMode.Album,
                                )
                            }
                        )
                    },
                    navigateBack = { navigate { navigateBack() } },
                    onSubtitleClicked = null,
                    onCloseSelection = ::onCloseSelection,
                    onLongClickOnMusic = ::onLongClickOnMusic,
                    onSearch = ::onSearch,
                    showMusicBottomSheet = ::showMusicBottomSheet,
                    onPlay = ::onPlay,
                    onShuffle = ::onShuffle,
                    onEdit = {
                        navigate {
                            toDestination(ModifyArtistDestination(detailId.artistId))
                        }
                    },
                    onCoverLoaded = ::onCoverLoaded,
                )
            }
        }

    private fun playlistFlow(
        detailId: MusicListDetailId.Playlist,
    ): Flow<MusicListDetailState> =
        combine(
            commonMusicUseCase.getPlaylistDuration(detailId.playlistId),
            commonPlaylistUseCase.getPlaylistPreview(detailId.playlistId),
            searchResult,
            playbackManager.getCachedPlaylist(detailId.playlistId.toString()),
        ) { duration, playlistPreview, searchMusics, cachedPlaylist ->
            when {
                playlistPreview == null -> MusicListDetailState.Error(
                    error = strings.playlistDoesNotExists,
                    navigateBack = { navigate { navigateBack() } },
                )
                else -> MusicListDetailState.Data(
                    cachedPlayedListUiSpec = cachedPlaylist?.toUiSpec(),
                    type = strings.playlistDetailTitle,
                    title = playlistPreview.name,
                    subTitle = strings.musics(playlistPreview.totalMusics),
                    cover = playlistPreview.cover,
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                    optionalContent = null,
                    musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
                    navigateBack = { navigate { navigateBack() } },
                    onSubtitleClicked = null,
                    onCloseSelection = ::onCloseSelection,
                    onLongClickOnMusic = ::onLongClickOnMusic,
                    onSearch = ::onSearch,
                    showMusicBottomSheet = ::showMusicBottomSheet,
                    onPlay = ::onPlay,
                    onShuffle = ::onShuffle,
                    onEdit = {
                        navigate {
                            toDestination(ModifyPlaylistDestination(detailId.playlistId))
                        }
                    },
                    onCoverLoaded = ::onCoverLoaded,
                )
            }
        }

    private fun monthFlow(
        detailId: MusicListDetailId.Month,
    ): Flow<MusicListDetailState> =
        combine(
            commonMusicUseCase.getMonthMusicPreview(month = detailId.month),
            commonMusicUseCase.getMonthMusicsDuration(detailId.month),
            searchResult,
            playbackManager.getCachedPlaylist(detailId.month),
        ) { monthMusicPreview, duration, searchMusics, cachedPlaylist ->
            when {
                monthMusicPreview == null -> MusicListDetailState.Error(
                    error = strings.monthPlaylistDoesNotExists,
                    navigateBack = { navigate { navigateBack() } },
                )
                else -> MusicListDetailState.Data(
                    cachedPlayedListUiSpec = cachedPlaylist?.toUiSpec(),
                    type = strings.monthDetailTitle,
                    title = monthMusicPreview.month,
                    subTitle = strings.musics(monthMusicPreview.totalMusics),
                    cover = monthMusicPreview.cover,
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                    optionalContent = null,
                    musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
                    navigateBack = { navigate { navigateBack() } },
                    onSubtitleClicked = null,
                    onCloseSelection = ::onCloseSelection,
                    onLongClickOnMusic = ::onLongClickOnMusic,
                    onSearch = ::onSearch,
                    showMusicBottomSheet = ::showMusicBottomSheet,
                    onPlay = ::onPlay,
                    onShuffle = ::onShuffle,
                    onEdit = null,
                    onCoverLoaded = ::onCoverLoaded,
                )
            }
        }

    private fun folderFlow(
        detailId: MusicListDetailId.Folder,
    ): Flow<MusicListDetailState> =
        combine(
            commonMusicUseCase.getMusicFolderPreview(folder = detailId.folder),
            commonMusicUseCase.getFolderMusicsDuration(detailId.folder),
            searchResult,
            playbackManager.getCachedPlaylist(detailId.folder),
        ) { musicFolderPreview, duration, searchMusics, cachedPlaylist ->
            when {
                musicFolderPreview == null -> MusicListDetailState.Error(
                    error = strings.folderDoesNotExists,
                    navigateBack = { navigate { navigateBack() } },
                )
                else -> MusicListDetailState.Data(
                    cachedPlayedListUiSpec = cachedPlaylist?.toUiSpec(),
                    type = strings.folderDetailTitle,
                    title = musicFolderPreview.folder,
                    subTitle = strings.musics(musicFolderPreview.totalMusics),
                    cover = musicFolderPreview.cover,
                    musics = musics,
                    duration = duration,
                    searchMusics = searchMusics,
                    optionalContent = null,
                    musicItemLeadingSpec = { MusicItemLeadingSpec.Cover },
                    navigateBack = { navigate { navigateBack() } },
                    onSubtitleClicked = null,
                    onCloseSelection = ::onCloseSelection,
                    onLongClickOnMusic = ::onLongClickOnMusic,
                    onSearch = ::onSearch,
                    showMusicBottomSheet = ::showMusicBottomSheet,
                    onPlay = ::onPlay,
                    onShuffle = ::onShuffle,
                    onEdit = null,
                    onCoverLoaded = ::onCoverLoaded,
                )
            }
        }

    private fun onCloseSelection() {
        multiSelectionManager.clearMultiSelection()
    }

    private fun showMusicBottomSheet(musicId: Uuid) {
        navigate {
            toDestination(
                MusicBottomSheetDestination(
                    musicIds = listOf(musicId),
                    playlistId = (detailId as? MusicListDetailId.Playlist)?.playlistId,
                )
            )
        }
    }

    private fun onSearch(search: String) {
        _searchQuery.value = search
    }

    private fun onLongClickOnMusic(musicId: Uuid) {
        multiSelectionManager.toggleElementInSelection(
            id = musicId.toString(),
            mode = SelectionMode.Music,
        )
    }

    private fun continuePlayedList(playedListId: Uuid) {
        viewModelScope.launch {
            playbackManager.continuePlayedList(playedListId)
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    private fun deletePlayedList(playedListId: Uuid) {
        viewModelScope.launch {
            playbackManager.deletePlayedList(playedListId)
        }
    }

    private fun onShuffle() {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getMusicsFromMusicListDetailId(detailId)

            if (musics.isNotEmpty()) {
                playbackManager.playShuffle(
                    musicList = musics,
                    playlistId = detailId.id,
                    isMain = false,
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    private fun onPlay(music: Music?) {
        viewModelScope.launch {
            val musics: List<Music> = commonMusicUseCase.getMusicsFromMusicListDetailId(detailId)

            if (musics.isNotEmpty()) {
                playbackManager.setCurrentPlaylistAndMusic(
                    music = music ?: musics.first(),
                    musicList = musics,
                    playlistId = detailId.id,
                    isMainPlaylist = false
                )
                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
            }
        }
    }

    private fun onCoverLoaded(cover: ImageBitmap?) {
        cover?.let { bitmap ->
            colorThemeManager.setNewPlaylistCover(
                playlistDetailCover = PlaylistDetailCover.fromImageBitmap(bitmap)
            )
        }
    }

    private fun PlayedListToContinue.toUiSpec(): CachedPlayedListUiSpec =
        CachedPlayedListUiSpec(
            playedListId = playedListId,
            currentMusic = currentMusic,
            onContinue = {
                continuePlayedList(playedListId)
            },
            onDismiss = {
                deletePlayedList(playedListId)
            }
        )
}