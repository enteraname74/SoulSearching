@file:Suppress("Deprecation")

package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.QuickAccessible
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.composables.dialog.CreatePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.ElementsVisibility
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllAlbumsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllArtistsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicFoldersState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllPlaylistsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllQuickAccessState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageNavigationState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.SearchAllState
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.GitHubReleaseBottomSheet
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.SoulMixDialog
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allAlbumsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allArtistsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allMusicFoldersTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allMusicsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allPlaylistsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allQuickAccessTab
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.feature.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedScreenFocusedElement
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.UUID

@Suppress("Deprecation")
class MainPageViewModel(
    viewSettingsManager: ViewSettingsManager,
    private val playbackManager: PlaybackManager,
    private val playerViewManager: PlayerViewManager,
    private val sortingInformationDelegateImpl: SortingInformationDelegateImpl,
    private val multiSelectionManager: MultiSelectionManager,
    getAllQuickAccessElementsUseCase: GetAllQuickAccessElementsUseCase,
) : ViewModel(), KoinComponent,
    SortingInformationDelegate by sortingInformationDelegateImpl {

    private val settings: SoulSearchingSettings by inject()
    private val deleteMusicUseCase: DeleteMusicUseCase by inject()
    private val feedbackPopUpManager: FeedbackPopUpManager by inject()

    private val commonMusicUseCase: CommonMusicUseCase by inject()
    private val commonAlbumUseCase: CommonAlbumUseCase by inject()
    private val commonArtistUseCase: CommonArtistUseCase by inject()
    private val commonCoverUseCase: CommonCoverUseCase by inject()
    private val commonPlaylistUseCase: CommonPlaylistUseCase by inject()
    private val commonFolderUseCase: CommonFolderUseCase by inject()
    private val commonReleaseUseCase: CommonReleaseUseCase by inject()
    private val shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val shouldShowNewVersionPin: StateFlow<Boolean> = shouldInformOfNewReleaseUseCase()
        .stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = false
        )

    val multiSelectionState: StateFlow<MultiSelectionState> = multiSelectionManager.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MultiSelectionState(emptyList())
        )

    private var _currentPage: MutableStateFlow<ElementEnum?> = MutableStateFlow(null)
    val currentPage: StateFlow<ElementEnum?> = _currentPage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tabs: StateFlow<List<PagerScreen>> =
        viewSettingsManager.visibleElements.mapLatest { elementsVisibility ->
            val elementEnums = buildListOfElementEnums(elementsVisibility = elementsVisibility)
            buildTabs(elements = elementEnums)
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val isUsingVerticalAccessBar: StateFlow<Boolean> = settings.getFlowOn(
        SoulSearchingSettingsKeys.MainPage.IS_USING_VERTICAL_ACCESS_BAR,
    ).stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Lazily,
        initialValue = true,
    )

    @OptIn(ExperimentalMaterialApi::class)
    val searchDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _musics: Flow<PagingData<Music>> = commonMusicUseCase
        .getAllPaged()
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artists = commonArtistUseCase
        .getAllPaged()
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _albums = commonAlbumUseCase
        .getAllPaged()
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _playlists = commonPlaylistUseCase
        .getAllPaged()
        .cachedIn(viewModelScope)

    private val _search: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchAllState: StateFlow<SearchAllState> = _search.flatMapLatest { search ->
        if (search.isNotBlank()) {
            combine(
                commonMusicUseCase.searchAll(search),
                commonAlbumUseCase.searchAll(search),
                commonArtistUseCase.searchAll(search),
                commonPlaylistUseCase.searchAll(search),
            ) { musics, albums, artists, playlists ->
                SearchAllState(
                    musics = musics,
                    albums = albums,
                    playlists = playlists,
                    artists = artists,
                )
            }
        } else {
            flowOf(SearchAllState())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        SearchAllState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allQuickAccessState: StateFlow<AllQuickAccessState> =
        getAllQuickAccessElementsUseCase().mapLatest { elements ->
            AllQuickAccessState(
                allQuickAccess = elements,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AllQuickAccessState()
        )

    val allMusicsState: StateFlow<AllMusicsState> = combine(
        musicSortingInformation,
        commonMusicUseCase.getAllMonthMusics(),
    ) { sortingInformation, allMonthMusics ->
        AllMusicsState(
            musics = _musics,
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
            monthMusicPreviews = allMonthMusics,
        )
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Lazily,
        initialValue = AllMusicsState(),
    )

    val allMusicFoldersState: StateFlow<AllMusicFoldersState> =
        commonMusicUseCase.getAllMusicFolders().map { allMusicFolders ->
            AllMusicFoldersState(
                allMusicFolders = allMusicFolders,
            )
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = AllMusicFoldersState()
        )

    val allArtistsState: StateFlow<AllArtistsState> =
        artistSortingInformation.map { sortingInformation ->
            AllArtistsState(
                artists = _artists,
                sortType = sortingInformation.type,
                sortDirection = sortingInformation.direction,
            )
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = AllArtistsState()
        )

    val allAlbumsState: StateFlow<AllAlbumsState> =
        albumSortingInformation.map { sortingInformation ->
            AllAlbumsState(
                albums = _albums,
                sortType = sortingInformation.type,
                sortDirection = sortingInformation.direction,
            )
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = AllAlbumsState()
        )

    val allPlaylistsState: StateFlow<AllPlaylistsState> =
        playlistSortingInformation.map { sortingInformation ->
            AllPlaylistsState(
                playlists = _playlists,
                sortType = sortingInformation.type,
                sortDirection = sortingInformation.direction,
            )
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = AllPlaylistsState()
        )

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

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
        if (!cleanMusicsLaunched) {
            coroutineScope.launch {
                cleanMusicsLaunched = true
                checkAndDeleteMusicIfNotExist()
            }
        }

        coroutineScope.launch {
            commonCoverUseCase.deleteUnusedFileCovers()
        }

        coroutineScope.launch {
            val allFolders = commonFolderUseCase.getAll().first()
            val foldersToDelete = allFolders.filter { !File(it.folderPath).exists() }
            commonFolderUseCase.deleteAll(foldersToDelete)
        }

        coroutineScope.launch {
            settings.getFlowOn(SoulSearchingSettingsKeys.Release.IS_FETCH_RELEASE_FROM_GITHUB_ENABLED)
                .collectLatest { isEnabled ->
                    if (isEnabled) {
                        commonReleaseUseCase.fetchLatest()
                    } else {
                        commonReleaseUseCase.deleteLatest()
                    }
                }
        }

        coroutineScope.launch {
            settings.getFlowOn(SoulSearchingSettingsKeys.Release.SHOULD_SHOW_RELEASE_BOTTOM_ENABLE_HINT)
                .collectLatest { shouldShow ->
                    if (shouldShow) {
                        _bottomSheetState.value = GitHubReleaseBottomSheet(
                            onClose = {
                                _bottomSheetState.value = null
                                settings.set(
                                    key = SoulSearchingSettingsKeys.Release.SHOULD_SHOW_RELEASE_BOTTOM_ENABLE_HINT.key,
                                    value = false,
                                )
                            },
                            onNavigateToGithubReleasePermission = {
                                navigateAndClearSelection(
                                    MainPageNavigationState.ToAdvancedSettings(
                                        focusedElement = SettingsAdvancedScreenFocusedElement
                                            .ReleasePermission,
                                    )
                                )
                            }
                        )
                    }
                }
        }
    }

    fun setCurrentPage(page: ElementEnum) {
        _currentPage.value = page
    }

    /**
     * Check all musics and delete the one that does not exist (if the path of the music is not valid).
     */
    fun checkAndDeleteMusicIfNotExist() {
        coroutineScope.launch {
            var deleteCount = 0
            // TODO OPTIMIZATION: Improve check?
            val all = commonMusicUseCase.getAll().first()
            for (music in all) {
                if (!File(music.path).exists()) {
                    playbackManager.removeSongsFromPlayedPlaylist(
                        musicIds = listOf(music.musicId)
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
                    viewModelScope.launch {
                        commonPlaylistUseCase.upsert(
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
        navigateAndClearSelection(
            when (quickAccessible) {
                is AlbumPreview -> MainPageNavigationState.ToAlbum(
                    albumId = quickAccessible.id,
                )

                is ArtistPreview -> MainPageNavigationState.ToArtist(
                    artistId = quickAccessible.id,
                )

                is Music -> MainPageNavigationState.Idle
                is PlaylistPreview -> MainPageNavigationState.ToPlaylist(
                    playlistId = quickAccessible.id,
                )
            }
        )
    }

    private fun showQuickAccessBottomSheet(quickAccessible: QuickAccessible) {
        coroutineScope.launch {
            when (quickAccessible) {
                is AlbumPreview -> showAlbumBottomSheet(
                    albumIds = listOf(quickAccessible.id),
                )

                is ArtistPreview -> showArtistBottomSheet(
                    artistIds = listOf(quickAccessible.id),
                )

                is Music -> showMusicBottomSheet(
                    musicIds = listOf(quickAccessible.musicId),
                )

                is PlaylistPreview -> showPlaylistBottomSheet(
                    playlistIds = listOf(quickAccessible.id),
                )
            }
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
                            navigateAndClearSelection(
                                MainPageNavigationState.ToPlaylist(
                                    playlistId = id,
                                )
                            )
                        }
                    )
                )

                ElementEnum.ALBUMS -> add(
                    allAlbumsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToAlbum = { id ->
                            navigateAndClearSelection(
                                MainPageNavigationState.ToAlbum(
                                    albumId = id,
                                )
                            )
                        }
                    )
                )

                ElementEnum.ARTISTS -> add(
                    allArtistsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToArtist = { id ->
                            navigateAndClearSelection(
                                MainPageNavigationState.ToArtist(
                                    artistId = id,
                                )
                            )
                        }
                    )
                )

                ElementEnum.MUSICS -> add(
                    allMusicsTab(
                        mainPageViewModel = this@MainPageViewModel,
                        navigateToMonth = { month ->
                            navigateAndClearSelection(
                                MainPageNavigationState.ToMonth(
                                    month = month,
                                )
                            )
                        },
                    )
                )

                ElementEnum.FOLDERS -> add(
                    allMusicFoldersTab(
                        state = allMusicFoldersState,
                        navigateToFolder = { folderPath ->
                            navigateAndClearSelection(
                                MainPageNavigationState.ToFolder(
                                    folderPath = folderPath,
                                )
                            )
                        },
                        showSoulMixDialog = ::showSoulMixDialog,
                        onSoulMixClicked = ::onSoulMixClicked
                    )
                )
            }
        }
    }

    private fun navigateAndClearSelection(state: MainPageNavigationState) {
        _navigationState.value = state
        multiSelectionManager.clearMultiSelection()
    }

    fun toPlaylist(playlistId: UUID) {
        navigateAndClearSelection(
            MainPageNavigationState.ToPlaylist(
                playlistId = playlistId,
            )
        )
    }

    fun toAlbum(albumId: UUID) {
        navigateAndClearSelection(
            MainPageNavigationState.ToAlbum(
                albumId = albumId,
            )
        )
    }

    fun toArtist(artistId: UUID) {
        navigateAndClearSelection(
            MainPageNavigationState.ToArtist(
                artistId = artistId,
            )
        )
    }

    fun toSettings() {
        navigateAndClearSelection(
            MainPageNavigationState.ToSettings
        )
    }

    fun onMusicClicked(music: Music) {
        coroutineScope.launch {
            // TODO OPTIMIZATION: find a way to not directly fetch all songs
            playbackManager.setCurrentPlaylistAndMusic(
                music = music,
                musicList = commonMusicUseCase.getAllSorted(),
                isMainPlaylist = true,
                playlistId = null,
            )
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    fun onPlayAll() {
        coroutineScope.launch {
            // TODO OPTIMIZATION: find a way to not directly fetch all songs
            playbackManager.playShuffle(musicList = commonMusicUseCase.getAllSorted())
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    fun onSoulMixClicked() {
        viewModelScope.launch {
            playbackManager.playSoulMix()
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    fun onSearch(search: String) {
        _search.value = search
    }

    fun showMusicBottomSheet(musicIds: List<UUID>) {
        _navigationState.value = MainPageNavigationState.ToMusicBottomSheet(musicIds = musicIds)
    }

    fun showPlaylistBottomSheet(playlistIds: List<UUID>) {
        _navigationState.value = MainPageNavigationState.ToPlaylistBottomSheet(
            playlistIds = playlistIds,
        )
    }

    fun showArtistBottomSheet(artistIds: List<UUID>) {
        _navigationState.value = MainPageNavigationState.ToArtistBottomSheet(
            artistIds = artistIds,
        )
    }

    fun showAlbumBottomSheet(albumIds: List<UUID>) {
        _navigationState.value = MainPageNavigationState.ToAlbumBottomSheet(
            albumIds = albumIds,
        )
    }

    fun clearMultiSelection() {
        multiSelectionManager.clearMultiSelection()
    }

    fun toggleElementInSelection(
        id: UUID,
        mode: SelectionMode,
    ) {
        multiSelectionManager.toggleElementInSelection(
            id = id,
            mode = mode,
        )
    }
}