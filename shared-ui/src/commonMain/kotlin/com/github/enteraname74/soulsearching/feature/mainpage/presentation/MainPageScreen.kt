package com.github.enteraname74.soulsearching.feature.mainpage.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.presentation.SelectedFolderScreen
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.presentation.SelectedMonthScreen
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.SelectedPlaylistScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.*
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.*
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainMenuHeaderComposable
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageVerticalShortcut
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab.*
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import com.github.enteraname74.soulsearching.feature.search.SearchAll
import com.github.enteraname74.soulsearching.feature.search.SearchView
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreen
import kotlinx.coroutines.launch

/**
 * Represent the view of the main page screen.
 */
@OptIn(ExperimentalMaterialApi::class)
class MainPageScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val allMusicsViewModel = getScreenModel<AllMusicsViewModel>()
        val allPlaylistsViewModel = getScreenModel<AllPlaylistsViewModel>()
        val allAlbumsViewModel = getScreenModel<AllAlbumsViewModel>()
        val allArtistsViewModel = getScreenModel<AllArtistsViewModel>()
        val allQuickAccessViewModel = getScreenModel<AllQuickAccessViewModel>()

        val musicState by allMusicsViewModel.state.collectAsState()
        val playlistState by allPlaylistsViewModel.state.collectAsState()
        val albumState by allAlbumsViewModel.state.collectAsState()
        val artistState by allArtistsViewModel.state.collectAsState()
        val quickAccessState by allQuickAccessViewModel.state.collectAsState()

        val searchDraggableState = allMusicsViewModel.searchDraggableState

        val musicBottomSheetState: SoulBottomSheet? by allMusicsViewModel.bottomSheetState.collectAsState()
        val addToPlaylistsBottomSheetState: AddToPlaylistBottomSheet? by allMusicsViewModel.addToPlaylistsBottomSheetState.collectAsState()
        val musicDialogState: SoulDialog? by allMusicsViewModel.dialogState.collectAsState()
        val musicNavigationState: AllMusicsNavigationState by allMusicsViewModel.navigationState.collectAsState()
        musicBottomSheetState?.BottomSheet()
        addToPlaylistsBottomSheetState?.BottomSheet()
        musicDialogState?.Dialog()

        val playlistBottomSheetState: SoulBottomSheet? by allPlaylistsViewModel.bottomSheetState.collectAsState()
        val playlistDialogState: SoulDialog? by allPlaylistsViewModel.dialogState.collectAsState()
        val playlistNavigationState: AllPlaylistsNavigationState by allPlaylistsViewModel.navigationState.collectAsState()
        playlistBottomSheetState?.BottomSheet()
        playlistDialogState?.Dialog()

        val albumBottomSheetState: SoulBottomSheet? by allAlbumsViewModel.bottomSheetState.collectAsState()
        val albumDialogState: SoulDialog? by allAlbumsViewModel.dialogState.collectAsState()
        val albumNavigationState: AllAlbumsNavigationState by allAlbumsViewModel.navigationState.collectAsState()
        albumBottomSheetState?.BottomSheet()
        albumDialogState?.Dialog()

        val artistBottomSheetState: SoulBottomSheet? by allArtistsViewModel.bottomSheetState.collectAsState()
        val artistDialogState: SoulDialog? by allArtistsViewModel.dialogState.collectAsState()
        val artistNavigationState: AllArtistsNavigationState by allArtistsViewModel.navigationState.collectAsState()
        artistBottomSheetState?.BottomSheet()
        artistDialogState?.Dialog()

        LaunchedEffect(musicNavigationState) {
            when (musicNavigationState) {
                AllMusicsNavigationState.Idle -> { /*no-op*/
                }

                is AllMusicsNavigationState.ToModifyMusic -> {
                    val musicToModify: Music =
                        (musicNavigationState as AllMusicsNavigationState.ToModifyMusic).selectedMusic
                    navigator.push(ModifyMusicScreen(selectedMusicId = musicToModify.musicId.toString()))
                    allMusicsViewModel.consumeNavigation()
                }
            }
        }
        LaunchedEffect(playlistNavigationState) {
            when (playlistNavigationState) {
                AllPlaylistsNavigationState.Idle -> { /*no-op*/
                }

                is AllPlaylistsNavigationState.ToModifyPlaylist -> {
                    val playlistToModify: Playlist =
                        (playlistNavigationState as AllPlaylistsNavigationState.ToModifyPlaylist).selectedPlaylist
                    navigator.push(ModifyPlaylistScreen(selectedPlaylistId = playlistToModify.playlistId.toString()))
                    allPlaylistsViewModel.consumeNavigation()
                }
            }
        }
        LaunchedEffect(albumNavigationState) {
            when (albumNavigationState) {
                AllAlbumsNavigationState.Idle -> { /*no-op*/
                }

                is AllAlbumsNavigationState.ToModifyAlbum -> {
                    val albumToModify: Album =
                        (albumNavigationState as AllAlbumsNavigationState.ToModifyAlbum).selectedAlbum
                    navigator.push(ModifyAlbumScreen(selectedAlbumId = albumToModify.albumId.toString()))
                    allAlbumsViewModel.consumeNavigation()
                }
            }
        }
        LaunchedEffect(artistNavigationState) {
            when (artistNavigationState) {
                AllArtistsNavigationState.Idle -> { /*no-op*/
                }

                is AllArtistsNavigationState.ToModifyArtist -> {
                    val artistToModify: Artist =
                        (artistNavigationState as AllArtistsNavigationState.ToModifyArtist).selectedArtist
                    navigator.push(ModifyArtistScreen(selectedArtistId = artistToModify.artistId.toString()))
                    allArtistsViewModel.consumeNavigation()
                }
            }
        }

        MainPageScreenView(
            allMusicsViewModel = allMusicsViewModel,
            allPlaylistsViewModel = allPlaylistsViewModel,
            allAlbumsViewModel = allAlbumsViewModel,
            allArtistsViewModel = allArtistsViewModel,
            navigateToPlaylist = { playlistId ->
                navigator.push(
                    SelectedPlaylistScreen(selectedPlaylistId = playlistId)
                )
            },
            navigateToAlbum = { albumId ->
                navigator.push(
                    SelectedAlbumScreen(selectedAlbumId = albumId)
                )
            },
            navigateToArtist = { artistId ->
                navigator.push(
                    SelectedArtistScreen(selectedArtistId = artistId)
                )
            },
            navigateToFolder = {
                navigator.push(
                    SelectedFolderScreen(it)
                )
            },
            navigateToMonth = {
                navigator.push(
                    SelectedMonthScreen(it)
                )
            },
            navigateToSettings = {
                navigator.push(
                    SettingsScreen()
                )
            },
            searchDraggableState = searchDraggableState,
            musicState = musicState,
            playlistState = playlistState,
            albumState = albumState,
            artistState = artistState,
            quickAccessState = quickAccessState
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
@Suppress("Deprecation")
fun MainPageScreenView(
    allMusicsViewModel: AllMusicsViewModel,
    allPlaylistsViewModel: AllPlaylistsViewModel,
    allAlbumsViewModel: AllAlbumsViewModel,
    allArtistsViewModel: AllArtistsViewModel,
    navigateToPlaylist: (playlistId: String) -> Unit,
    navigateToAlbum: (albumId: String) -> Unit,
    navigateToArtist: (artistId: String) -> Unit,
    navigateToFolder: (folderPath: String) -> Unit,
    navigateToMonth: (month: String) -> Unit,
    navigateToSettings: () -> Unit,
    searchDraggableState: SwipeableState<BottomSheetStates>,
    musicState: AllMusicsState,
    playlistState: PlaylistState,
    albumState: AlbumState,
    artistState: ArtistState,
    quickAccessState: QuickAccessState,
    viewSettingsManager: ViewSettingsManager = injectElement(),
) {
    val windowSize = rememberWindowSize()

    val visibleElements = viewSettingsManager.getListOfVisibleElements()
    val pagerScreens = ArrayList<PagerScreen>()
    visibleElements.forEach { element ->
        pagerScreens.add(
            when (element) {
                ElementEnum.QUICK_ACCESS -> allQuickAccessTab(
                    quickAccessState = quickAccessState,
                    navigateToPlaylist = navigateToPlaylist,
                    navigateToArtist = navigateToArtist,
                    navigateToAlbum = navigateToAlbum,
                    artistBottomSheetAction = allArtistsViewModel::showArtistBottomSheet,
                    playlistBottomSheetAction = allPlaylistsViewModel::showPlaylistBottomSheet,
                    albumBottomSheetAction = allAlbumsViewModel::showAlbumBottomSheet,
                    musicBottomSheetAction = allMusicsViewModel::showMusicBottomSheet,
                )

                ElementEnum.PLAYLISTS -> allPlaylistsTab(
                    allPlaylistsViewModel = allPlaylistsViewModel,
                    playlistState = playlistState,
                    navigateToPlaylist = navigateToPlaylist,
                )

                ElementEnum.ALBUMS -> allAlbumsTab(
                    allAlbumsViewModel = allAlbumsViewModel,
                    albumState = albumState,
                    navigateToAlbum = navigateToAlbum,
                )

                ElementEnum.ARTISTS -> allArtistsTab(
                    allArtistsViewModel = allArtistsViewModel,
                    artistState = artistState,
                    navigateToArtist = navigateToArtist,
                )

                ElementEnum.MUSICS -> allMusicsTab(
                    allMusicsViewModel = allMusicsViewModel,
                    state = musicState,
                    navigateToFolder = navigateToFolder,
                    navigateToMonth = navigateToMonth,
                )
            }
        )
    }

    val pagerState = rememberPagerState(
        pageCount = { pagerScreens.size }
    )

    LaunchedEffect(allMusicsViewModel.currentPage) {
        val vmCurrentPager = visibleElements.indexOf(allMusicsViewModel.currentPage)
        if (vmCurrentPager != pagerState.currentPage && vmCurrentPager != -1) {
            pagerState.animateScrollToPage(vmCurrentPager)
        }
    }

    val searchBarFocusRequester = remember { FocusRequester() }
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SoulSearchingColorTheme.colorScheme.primary)
    ) {
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SoulSearchingColorTheme.colorScheme.primary)
        ) {
            MainMenuHeaderComposable(
                settingsAction = navigateToSettings,
                searchAction = {
                    coroutineScope.launch {
                        searchDraggableState.animateTo(
                            BottomSheetStates.EXPANDED,
                            tween(UiConstants.AnimationDuration.normal)
                        )
                    }.invokeOnCompletion {
                        searchBarFocusRequester.requestFocus()
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                // We only show the vertical shortcut if there is more than one panel to access.
                if (visibleElements.size > 1 && windowSize != WindowSize.Large) {
                    MainPageVerticalShortcut(
                        currentPage = currentPage,
                        visibleElements = visibleElements,
                        switchPageAction = {
                            coroutineScope.launch {
                                if (it != -1) {
                                    pagerState.animateScrollToPage(it)
                                    allMusicsViewModel.currentPage = visibleElements[it]
                                }
                            }
                        }
                    )
                }

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) {
                    pagerScreens[it].screen()
                }
            }
        }

        SearchView(
            draggableState = searchDraggableState,
            placeholder = strings.searchAll,
            maxHeight = maxHeight,
            focusRequester = searchBarFocusRequester
        ) { searchText, focusManager ->
            SearchAll(
                searchText = searchText,
                retrieveCoverMethod = musicState.allCovers::getFromCoverId,
                musicState = musicState,
                albumState = albumState,
                artistState = artistState,
                playlistState = playlistState,
                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                navigateToPlaylist = navigateToPlaylist,
                navigateToArtist = navigateToArtist,
                navigateToAlbum = navigateToAlbum,
                isMainPlaylist = false,
                focusManager = focusManager,
                onSelectedMusicForBottomSheet = allMusicsViewModel::showMusicBottomSheet,
                onSelectedAlbumForBottomSheet = allAlbumsViewModel::showAlbumBottomSheet,
                onSelectedArtistForBottomSheet = allArtistsViewModel::showArtistBottomSheet,
                onSelectedPlaylistForBottomSheet = allPlaylistsViewModel::showPlaylistBottomSheet,
            )
        }
    }
}

