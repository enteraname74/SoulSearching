package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.album.GetAllAlbumWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.month.GetAllMonthMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetAllMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.soulsearching.commondelegate.*
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.ElementsVisibility
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.*
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.CreatePlaylistDialog
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.SoulMixDialog
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.*
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

@Suppress("Deprecation")
class MainPageViewModel(
    viewSettingsManager: ViewSettingsManager,
    private val playbackManager: PlaybackManager,
    private val getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
    private val musicFetcher: MusicFetcher,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val sortingInformationDelegateImpl: SortingInformationDelegateImpl,
    private val artistBottomSheetDelegateImpl: ArtistBottomSheetDelegateImpl,
    private val getAllArtistWithMusicsSortedUseCase: GetAllArtistWithMusicsSortedUseCase,
    private val getAllAlbumWithMusicsSortedUseCase: GetAllAlbumWithMusicsSortedUseCase,
    private val getAllPlaylistWithMusicsSortedUseCase: GetAllPlaylistWithMusicsSortedUseCase,
    private val playlistBottomSheetDelegateImpl: PlaylistBottomSheetDelegateImpl,
    private val albumBottomSheetDelegateImpl: AlbumBottomSheetDelegateImpl,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    getAllMonthMusicUseCase: GetAllMonthMusicUseCase,
    getAllMusicFolderListUseCase: GetAllMusicFolderListUseCase,
    getAllQuickAccessElementsUseCase: GetAllQuickAccessElementsUseCase,
) : ScreenModel,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl,
    ArtistBottomSheetDelegate by artistBottomSheetDelegateImpl,
    AlbumBottomSheetDelegate by albumBottomSheetDelegateImpl,
    PlaylistBottomSheetDelegate by playlistBottomSheetDelegateImpl,
    SortingInformationDelegate by sortingInformationDelegateImpl {

    private var _currentPage: MutableStateFlow<ElementEnum?> = MutableStateFlow(null)
    val currentPage: StateFlow<ElementEnum?> = _currentPage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tabs: StateFlow<List<PagerScreen>> = viewSettingsManager.visibleElements.mapLatest { elementsVisibility ->
        val elementEnums = buildListOfElementEnums(elementsVisibility = elementsVisibility)
        buildTabs(elements = elementEnums)
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    @OptIn(ExperimentalMaterialApi::class)
    val searchDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    private val allPlaylistsForBottomSheet: StateFlow<List<PlaylistWithMusics>> = getAllPlaylistWithMusicsUseCase()
        .stateIn(
            scope = screenModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _musics = musicSortingInformation.flatMapLatest { sortingInformation ->
        getAllMusicsSortedUseCase(
            sortDirection = sortingInformation.direction,
            sortType = sortingInformation.type,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artists = artistSortingInformation.flatMapLatest { sortingInformation ->
        getAllArtistWithMusicsSortedUseCase(
            sortDirection = sortingInformation.direction,
            sortType = sortingInformation.type,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _albums = albumSortingInformation.flatMapLatest { sortingInformation ->
        getAllAlbumWithMusicsSortedUseCase(
            sortDirection = sortingInformation.direction,
            sortType = sortingInformation.type,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _playlists = playlistSortingInformation.flatMapLatest { sortingInformation ->
        getAllPlaylistWithMusicsSortedUseCase(
            sortDirection = sortingInformation.direction,
            sortType = sortingInformation.type,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allQuickAccessState: StateFlow<AllQuickAccessState> = getAllQuickAccessElementsUseCase().mapLatest { elements ->
        AllQuickAccessState(
            allQuickAccess = elements,
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        AllQuickAccessState()
    )

    val allMusicsState: StateFlow<AllMusicsState> = combine(
        _musics,
        musicSortingInformation,
        getAllMonthMusicUseCase(),
    ) { musics, sortingInformation, allMonthMusics ->
        AllMusicsState(
            musics = musics,
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
            monthMusics = allMonthMusics,
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllMusicsState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allMusicFoldersState: StateFlow<AllMusicFoldersState> = getAllMusicFolderListUseCase().mapLatest { allMusicFolders ->
        AllMusicFoldersState(
            allMusicFolders = allMusicFolders,
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllMusicFoldersState()
    )

    val allArtistsState: StateFlow<AllArtistsState> = combine(
        _artists,
        artistSortingInformation,
    ) { artists, sortingInformation ->
        AllArtistsState(
            artists = artists,
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllArtistsState()
    )

    val allAlbumsState: StateFlow<AllAlbumsState> = combine(
        _albums,
        albumSortingInformation,
    ) { albums, sortingInformation ->
        AllAlbumsState(
            albums = albums,
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllAlbumsState()
    )

    val allPlaylistsState: StateFlow<AllPlaylistsState> = combine(
        _playlists,
        playlistSortingInformation,
    ) { playlists, sortingInformation ->
        AllPlaylistsState(
            playlists = playlists.map { it.toPlaylistWithMusicsNumber() },
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllPlaylistsState()
    )

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistsBottomSheetState: MutableStateFlow<AddToPlaylistBottomSheet?> = MutableStateFlow(null)
    val addToPlaylistsBottomSheetState: StateFlow<AddToPlaylistBottomSheet?> =
        _addToPlaylistsBottomSheetState.asStateFlow()

    private val _dialogState: MutableStateFlow<SoulDialog?> = MutableStateFlow(null)
    val dialogState: StateFlow<SoulDialog?> = _dialogState.asStateFlow()

    private val _navigationState: MutableStateFlow<MainPageNavigationState> = MutableStateFlow(
        MainPageNavigationState.Idle
    )
    val navigationState: StateFlow<MainPageNavigationState> = _navigationState.asStateFlow()

    fun consumeNavigation() {
        _navigationState.value = MainPageNavigationState.Idle
    }

    private var cleanMusicsLaunched: Boolean = false

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistsBottomSheetState.value = it },
            getAllPlaylistsWithMusics = { allPlaylistsForBottomSheet.value },
            onModifyMusic = { _navigationState.value = MainPageNavigationState.ToModifyMusic(it.musicId) }
        )

        artistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyArtist = { _navigationState.value = MainPageNavigationState.ToModifyArtist(it.artistId) }
        )

        albumBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyAlbum = { _navigationState.value = MainPageNavigationState.ToModifyAlbum(it.albumId) }
        )

        playlistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyPlaylist = { _navigationState.value = MainPageNavigationState.ToModifyPlaylist(it.playlistId) }
        )

        CoroutineScope(Dispatchers.IO).launch {
            allMusicsState.collect { musicState ->
                if (musicState.musics.isNotEmpty() && !cleanMusicsLaunched) {
                    CoroutineScope(Dispatchers.IO).launch {
                        cleanMusicsLaunched = true
                        checkAndDeleteMusicIfNotExist()
                    }
                }
            }
        }
    }

    fun setCurrentPage(page: ElementEnum) {
        _currentPage.value = page
    }

    /**
     * Fetch all musics.
     */
    suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
        finishAction: () -> Unit
    ) {
        musicFetcher.fetchMusics(
            updateProgress = updateProgress,
            finishAction = finishAction
        )
    }

    /**
     * Check all musics and delete the one that does not exist (if the path of the music is not valid).
     */
    fun checkAndDeleteMusicIfNotExist() {
        CoroutineScope(Dispatchers.IO).launch {
            var deleteCount = 0
            for (music in allMusicsState.value.musics) {
                if (!File(music.path).exists()) {
                    playbackManager.removeSongFromPlayedPlaylist(
                        music.musicId
                    )
                    deleteMusicUseCase(music = music)
                    deleteCount += 1
                }
            }

            if (deleteCount > 0) {
                feedbackPopUpManager.showFeedback(
                    feedback = strings.deletedMusicsAutomatically(total = deleteCount)
                )
            }
        }
    }

    /**
     * Show or hide the create playlist dialog.
     */
    fun showCreatePlaylistDialog() {
        _dialogState.value = CreatePlaylistDialog(
            onDismiss = { _dialogState.value = null },
            onConfirm = { playlistName ->
                if (playlistName.isNotBlank()) {
                    screenModelScope.launch {
                        upsertPlaylistUseCase(
                            playlist = Playlist(name = playlistName)
                        )
                    }
                }
                _dialogState.value = null
            }
        )
    }

    private fun showSoulMixDialog() {
        _dialogState.value = SoulMixDialog(
            onDismiss = { _dialogState.value = null },
        )
    }

    private fun navigateToQuickAccessible(quickAccessible: QuickAccessible) {
        _navigationState.value = when (quickAccessible) {
            is AlbumWithArtist -> MainPageNavigationState.ToAlbum(
                albumId = quickAccessible.album.albumId,
            )

            is ArtistWithMusics -> MainPageNavigationState.ToArtist(
                artistId = quickAccessible.artist.artistId,
            )

            is Music -> MainPageNavigationState.Idle
            is PlaylistWithMusicsNumber -> MainPageNavigationState.ToPlaylist(
                playlistId = quickAccessible.playlist.playlistId,
            )
        }
    }

    private fun showQuickAccessBottomSheet(quickAccessible: QuickAccessible) {
        when (quickAccessible) {
            is AlbumWithArtist -> showAlbumBottomSheet(
                album = quickAccessible.album,
            )

            is ArtistWithMusics -> showArtistBottomSheet(
                selectedArtist = quickAccessible,
            )

            is Music -> showMusicBottomSheet(
                selectedMusic = quickAccessible,
            )

            is PlaylistWithMusicsNumber -> showPlaylistBottomSheet(
                selectedPlaylist = quickAccessible.playlist,
            )
        }
    }

    private fun buildListOfElementEnums(
        elementsVisibility: ElementsVisibility,
    ): List<ElementEnum> = buildList {
        if (elementsVisibility.isQuickAccessShown) add(ElementEnum.QUICK_ACCESS)
        add(ElementEnum.MUSICS)
        if (elementsVisibility.arePlaylistsShown) add(ElementEnum.PLAYLISTS)
        if (elementsVisibility.areAlbumsShown) add(ElementEnum.ALBUMS)
        if (elementsVisibility.areArtistsShown) add(ElementEnum.ARTISTS)
        if (elementsVisibility.areMusicFoldersShown) add(ElementEnum.FOLDERS)
    }

    private fun buildTabs(elements: List<ElementEnum>): List<PagerScreen> = buildList {
        elements.forEach { element ->
            when (element) {
                ElementEnum.QUICK_ACCESS -> add(
                    allQuickAccessTab(
                        mainPageViewModel = this@MainPageViewModel,
                        onClick = ::navigateToQuickAccessible,
                        showQuickAccessBottomSheet = ::showQuickAccessBottomSheet,
                    )
                )

                ElementEnum.PLAYLISTS -> add(
                    allPlaylistsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToPlaylist = { id ->
                            _navigationState.value = MainPageNavigationState.ToPlaylist(
                                playlistId = id,
                            )
                        }
                    )
                )

                ElementEnum.ALBUMS -> add(
                    allAlbumsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToAlbum = { id ->
                            _navigationState.value = MainPageNavigationState.ToAlbum(
                                albumId = id,
                            )
                        }
                    )
                )

                ElementEnum.ARTISTS -> add(
                    allArtistsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToArtist = { id ->
                            _navigationState.value = MainPageNavigationState.ToArtist(
                                artistId = id,
                            )
                        }
                    )
                )

                ElementEnum.MUSICS -> add(
                    allMusicsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToMonth = { month ->
                            _navigationState.value = MainPageNavigationState.ToMonth(
                                month = month,
                            )
                        },
                    )
                )

                ElementEnum.FOLDERS -> add(
                    allMusicFoldersTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToFolder = { folderPath ->
                            _navigationState.value = MainPageNavigationState.ToFolder(
                                folderPath = folderPath,
                            )
                        },
                        showSoulMixDialog = ::showSoulMixDialog,
                    )
                )
            }
        }
    }
}