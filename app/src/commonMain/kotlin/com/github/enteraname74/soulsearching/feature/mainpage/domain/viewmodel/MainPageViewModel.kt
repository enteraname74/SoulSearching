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
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.QuickAccessible
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.cover.IsCoverUsedUseCase
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.month.GetAllMonthMusicUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetAllMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.AlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.ArtistBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.ArtistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MultiAlbumBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MultiArtistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MultiMusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MultiPlaylistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.MusicBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.commondelegate.PlaylistBottomSheetDelegate
import com.github.enteraname74.soulsearching.commondelegate.PlaylistBottomSheetDelegateImpl
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.CreatePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
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
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.GitHubReleaseBottomSheet
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.SoulMixDialog
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allAlbumsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allArtistsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allMusicFoldersTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allMusicsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allPlaylistsTab
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.allQuickAccessTab
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
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
    private val isCoverUsedUseCase: IsCoverUsedUseCase,
    private val musicBottomSheetDelegateImpl: MusicBottomSheetDelegateImpl,
    private val sortingInformationDelegateImpl: SortingInformationDelegateImpl,
    private val artistBottomSheetDelegateImpl: ArtistBottomSheetDelegateImpl,
    private val getAllPlaylistWithMusicsSortedUseCase: GetAllPlaylistWithMusicsSortedUseCase,
    private val playlistBottomSheetDelegateImpl: PlaylistBottomSheetDelegateImpl,
    private val albumBottomSheetDelegateImpl: AlbumBottomSheetDelegateImpl,
    val multiSelectionManagerImpl: MultiSelectionManagerImpl,
    getAllMonthMusicUseCase: GetAllMonthMusicUseCase,
    getAllMusicFolderListUseCase: GetAllMusicFolderListUseCase,
    getAllQuickAccessElementsUseCase: GetAllQuickAccessElementsUseCase,
) : ViewModel(), KoinComponent,
    MusicBottomSheetDelegate by musicBottomSheetDelegateImpl,
    ArtistBottomSheetDelegate by artistBottomSheetDelegateImpl,
    AlbumBottomSheetDelegate by albumBottomSheetDelegateImpl,
    PlaylistBottomSheetDelegate by playlistBottomSheetDelegateImpl,
    SortingInformationDelegate by sortingInformationDelegateImpl,
    MultiSelectionManager by multiSelectionManagerImpl {

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

    private val multiMusicBottomSheetDelegateImpl: MultiMusicBottomSheetDelegateImpl by inject()
    private val multiAlbumBottomSheetDelegateImpl: MultiAlbumBottomSheetDelegateImpl by inject()
    private val multiArtistBottomSheetDelegateImpl: MultiArtistBottomSheetDelegateImpl by inject()
    private val multiPlaylistBottomSheetDelegateImpl: MultiPlaylistBottomSheetDelegateImpl by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val shouldShowNewVersionPin: StateFlow<Boolean> = shouldInformOfNewReleaseUseCase()
        .stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val multiSelectionState: StateFlow<MultiSelectionState> = multiSelectionManagerImpl.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
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
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val isUsingVerticalAccessBar: StateFlow<Boolean> = settings.getFlowOn(
        SoulSearchingSettingsKeys.MainPage.IS_USING_VERTICAL_ACCESS_BAR,
    ).stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    @OptIn(ExperimentalMaterialApi::class)
    val searchDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    private val allPlaylistsForBottomSheet: StateFlow<List<PlaylistWithMusics>> =
        commonPlaylistUseCase.getAllWithMusics()
            .stateIn(
                scope = viewModelScope.plus(Dispatchers.IO),
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

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
    private val _playlists = playlistSortingInformation.flatMapLatest { sortingInformation ->
        getAllPlaylistWithMusicsSortedUseCase(
            sortDirection = sortingInformation.direction,
            sortType = sortingInformation.type,
        )
    }

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
        getAllMonthMusicUseCase(),
    ) { sortingInformation, allMonthMusics ->
        AllMusicsState(
            musics = _musics,
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
            monthMusics = allMonthMusics,
        )
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllMusicsState(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allMusicFoldersState: StateFlow<AllMusicFoldersState> =
        getAllMusicFolderListUseCase().mapLatest { allMusicFolders ->
            AllMusicFoldersState(
                allMusicFolders = allMusicFolders.sortedBy { it.name },
            )
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Eagerly,
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
            started = SharingStarted.Eagerly,
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
            started = SharingStarted.Eagerly,
            initialValue = AllAlbumsState()
        )

    val allPlaylistsState: StateFlow<AllPlaylistsState> = combine(
        _playlists,
        playlistSortingInformation,
    ) { playlists, sortingInformation ->
        AllPlaylistsState(
            playlists = playlists.map { it.toPlaylistPreview() },
            sortType = sortingInformation.type,
            sortDirection = sortingInformation.direction,
        )
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = AllPlaylistsState()
    )

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    private val _addToPlaylistsBottomSheetState: MutableStateFlow<AddToPlaylistBottomSheet?> =
        MutableStateFlow(null)
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
        multiSelectionManagerImpl.clearMultiSelection()
    }

    private var cleanMusicsLaunched: Boolean = false

    init {
        musicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistsBottomSheetState.value = it },
            getAllPlaylistsWithMusics = { allPlaylistsForBottomSheet.value },
            onModifyMusic = {
                _navigationState.value = MainPageNavigationState.ToModifyMusic(it.musicId)
            },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        artistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyArtist = {
                _navigationState.value = MainPageNavigationState.ToModifyArtist(it.artistId)
            },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        albumBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyAlbum = {
                _navigationState.value = MainPageNavigationState.ToModifyAlbum(it.albumId)
            },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        playlistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            onModifyPlaylist = {
                _navigationState.value = MainPageNavigationState.ToModifyPlaylist(it.playlistId)
            },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiMusicBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            setAddToPlaylistBottomSheetState = { _addToPlaylistsBottomSheetState.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiAlbumBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiArtistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        multiPlaylistBottomSheetDelegateImpl.initDelegate(
            setDialogState = { _dialogState.value = it },
            setBottomSheetState = { _bottomSheetState.value = it },
            multiSelectionManagerImpl = multiSelectionManagerImpl,
        )

        coroutineScope.launch {
            if (!cleanMusicsLaunched) {
                coroutineScope.launch {
                    cleanMusicsLaunched = true
                    checkAndDeleteMusicIfNotExist()
                }
            }
        }

        coroutineScope.launch {
            val allCoverIds: List<UUID> = commonCoverUseCase.getAllCoverIds()
            allCoverIds.forEach { coverId ->
                if (!isCoverUsedUseCase(coverId = coverId)) {
                    commonCoverUseCase.delete(coverId = coverId)
                }
            }
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
                                _navigationState.value = MainPageNavigationState.ToAdvancedSettings(
                                    focusedElement = SettingsAdvancedScreenFocusedElement.ReleasePermission,
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
        _navigationState.value = when (quickAccessible) {
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
    }

    private fun showQuickAccessBottomSheet(quickAccessible: QuickAccessible) {
        coroutineScope.launch {
            when (quickAccessible) {
                is AlbumPreview -> commonAlbumUseCase
                    .getAlbumWithMusics(albumId = quickAccessible.id)
                    .firstOrNull()?.let {
                        showAlbumBottomSheet(albumWithMusics = it)
                    }

                is ArtistPreview -> commonArtistUseCase
                    .getArtistWithMusic(artistId = quickAccessible.id)
                    .firstOrNull()?.let {
                        showArtistBottomSheet(selectedArtist = it)
                    }

                is Music -> showMusicBottomSheet(
                    selectedMusic = quickAccessible,
                )

                is PlaylistPreview -> commonPlaylistUseCase
                    .getWithMusics(playlistId = quickAccessible.id)
                    .firstOrNull()?.let {
                        showPlaylistBottomSheet(selectedPlaylist = it)
                    }
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

    private suspend fun handleMultiSelectionMusicBottomSheet() {
        val selectedIds = multiSelectionState.value.selectedIds
        if (selectedIds.size == 1) {
            val selectedMusic: Music =
                commonMusicUseCase.getFromId(musicId = selectedIds[0]).firstOrNull() ?: return
            showMusicBottomSheet(selectedMusic = selectedMusic)
        } else {
            multiMusicBottomSheetDelegateImpl.showMultiMusicBottomSheet()
        }
    }

    private suspend fun handleMultiSelectionAlbumBottomSheet() {
        val selectedIds = multiSelectionState.value.selectedIds
        if (selectedIds.size == 1) {
            val selectedAlbum: AlbumWithMusics =
                commonAlbumUseCase.getAlbumWithMusics(albumId = selectedIds[0]).firstOrNull()
                    ?: return
            showAlbumBottomSheet(albumWithMusics = selectedAlbum)
        } else {
            multiAlbumBottomSheetDelegateImpl.showMultiAlbumBottomSheet()
        }
    }

    private suspend fun handleMultiSelectionArtistBottomSheet() {
        val selectedIds = multiSelectionState.value.selectedIds
        if (selectedIds.size == 1) {
            val selectedArtist: ArtistWithMusics =
                commonArtistUseCase.getArtistWithMusic(artistId = selectedIds[0]).firstOrNull()
                    ?: return
            showArtistBottomSheet(selectedArtist = selectedArtist)
        } else {
            multiArtistBottomSheetDelegateImpl.showMultiArtistBottomSheet()
        }
    }

    private suspend fun handleMultiSelectionPlaylistBottomSheet() {
        val selectedIds = multiSelectionState.value.selectedIds
        if (selectedIds.size == 1) {
            val selectedPlaylist: PlaylistWithMusics =
                commonPlaylistUseCase.getWithMusics(playlistId = selectedIds[0]).firstOrNull()
                    ?: return
            showPlaylistBottomSheet(selectedPlaylist = selectedPlaylist)
        } else {
            multiPlaylistBottomSheetDelegateImpl.showMultiPlaylistBottomSheet()
        }
    }

    fun toPlaylist(playlistId: UUID) {
        _navigationState.value = MainPageNavigationState.ToPlaylist(
            playlistId = playlistId,
        )
    }

    fun toAlbum(albumId: UUID) {
        _navigationState.value = MainPageNavigationState.ToAlbum(
            albumId = albumId,
        )
    }

    fun toArtist(artistId: UUID) {
        _navigationState.value = MainPageNavigationState.ToArtist(
            artistId = artistId,
        )
    }

    fun toSettings() {
        _navigationState.value = MainPageNavigationState.ToSettings
    }

    fun handleMultiSelectionBottomSheet() {
        viewModelScope.launch {
            when (multiSelectionManagerImpl.selectionMode) {
                SelectionMode.Music -> handleMultiSelectionMusicBottomSheet()
                SelectionMode.Playlist -> handleMultiSelectionPlaylistBottomSheet()
                SelectionMode.Album -> handleMultiSelectionAlbumBottomSheet()
                SelectionMode.Artist -> handleMultiSelectionArtistBottomSheet()
            }
        }
    }

    fun onMusicClicked(music: Music) {
        coroutineScope.launch {
            // TODO OPTIMIZATION: find a way to not directly fetch all songs
            playbackManager.setCurrentPlaylistAndMusic(
                music = music,
                musicList = commonMusicUseCase.getAll().first(),
                isMainPlaylist = true,
                playlistId = null,
            )
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    fun onPlayAll() {
        coroutineScope.launch {
            // TODO OPTIMIZATION: find a way to not directly fetch all songs
            playbackManager.playShuffle(musicList = commonMusicUseCase.getAll().first())
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    fun showAlbumPreviewBottomSheet(albumPreview: AlbumPreview) {
        viewModelScope.launch {
            val albumWithMusics: AlbumWithMusics = commonAlbumUseCase
                .getAlbumWithMusics(albumPreview.id).firstOrNull() ?: return@launch

            showAlbumBottomSheet(albumWithMusics)
        }
    }

    fun showArtistPreviewBottomSheet(artistPreview: ArtistPreview) {
        viewModelScope.launch {
            val artistWithMusics: ArtistWithMusics = commonArtistUseCase
                .getArtistWithMusic(artistPreview.id).firstOrNull() ?: return@launch

            showArtistBottomSheet(artistWithMusics)
        }
    }

    fun showPlaylistPreviewBottomSheet(playlistPreview: PlaylistPreview) {
        viewModelScope.launch {
            val playlistWithMusics: PlaylistWithMusics = commonPlaylistUseCase
                .getWithMusics(playlistPreview.id).firstOrNull() ?: return@launch

            showPlaylistBottomSheet(playlistWithMusics)
        }
    }
}