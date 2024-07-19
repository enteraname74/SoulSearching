package com.github.enteraname74.soulsearching.feature.mainpage.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.events.AlbumEvent
import com.github.enteraname74.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.domain.events.MusicEvent
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.SelectedPlaylistScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.*
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.*
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.*
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchAll
import com.github.enteraname74.soulsearching.feature.search.SearchView
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreen
import kotlinx.coroutines.launch
import java.util.*

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
        val allImageCoversViewModel = getScreenModel<AllImageCoversViewModel>()
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
            when(musicNavigationState) {
                AllMusicsNavigationState.Idle -> { /*no-op*/ }
                is AllMusicsNavigationState.ToModifyMusic -> {
                    val musicToModify: Music = (musicNavigationState as AllMusicsNavigationState.ToModifyMusic).selectedMusic
                    navigator.push(ModifyMusicScreen(selectedMusicId = musicToModify.musicId.toString()))
                    allMusicsViewModel.consumeNavigation()
                }
            }
        }
        LaunchedEffect(playlistNavigationState) {
            when(playlistNavigationState) {
                AllPlaylistsNavigationState.Idle -> { /*no-op*/ }
                is AllPlaylistsNavigationState.ToModifyPlaylist -> {
                    val playlistToModify: Playlist =
                        (playlistNavigationState as AllPlaylistsNavigationState.ToModifyPlaylist).selectedPlaylist
                    navigator.push(ModifyPlaylistScreen(selectedPlaylistId = playlistToModify.playlistId.toString()))
                    allPlaylistsViewModel.consumeNavigation()
                }
            }
        }
        LaunchedEffect(albumNavigationState) {
            when(albumNavigationState) {
                AllAlbumsNavigationState.Idle -> { /*no-op*/ }
                is AllAlbumsNavigationState.ToModifyAlbum -> {
                    val albumToModify: Album =
                        (albumNavigationState as AllAlbumsNavigationState.ToModifyAlbum).selectedAlbum
                    navigator.push(ModifyAlbumScreen(selectedAlbumId = albumToModify.albumId.toString()))
                    allAlbumsViewModel.consumeNavigation()
                }
            }
        }
        LaunchedEffect(artistNavigationState) {
            when(artistNavigationState) {
                AllArtistsNavigationState.Idle -> { /*no-op*/ }
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
            allImageCoversViewModel = allImageCoversViewModel,
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
    allImageCoversViewModel: AllImageCoversViewModel,
    navigateToPlaylist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToSettings: () -> Unit,
    searchDraggableState: SwipeableState<BottomSheetStates>,
    musicState: MainPageState,
    playlistState: PlaylistState,
    albumState: AlbumState,
    artistState: ArtistState,
    quickAccessState: QuickAccessState,
    viewSettingsManager: ViewSettingsManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedAlbumId by rememberSaveable {
        mutableStateOf<UUID?>(null)
    }

    if (playlistState.isCreatePlaylistDialogShown) {
        CreatePlaylistDialog(onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SoulSearchingColorTheme.colorScheme.primary)
    ) {
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        val searchBarFocusRequester = remember { FocusRequester() }

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

            val visibleElements = viewSettingsManager.getListOfVisibleElements()
            val pagerScreens = ArrayList<PagerScreen>()
            visibleElements.forEach { element ->
                pagerScreens.add(
                    when (element) {
                        ElementEnum.QUICK_ACCESS -> PagerScreen(
                            title = strings.quickAccess,
                            screen = {
                                AllElementsComposable(
                                    retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                                    list = quickAccessState.allQuickAccess,
                                    title = strings.quickAccess,
                                    isUsingSort = false,
                                    navigateToArtist = navigateToArtist,
                                    artistBottomSheetAction = {
                                        coroutineScope.launch {
                                            allArtistsViewModel.onArtistEvent(
                                                ArtistEvent.SetSelectedArtistWithMusics(
                                                    it
                                                )
                                            )
                                            allArtistsViewModel.onArtistEvent(
                                                ArtistEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    navigateToPlaylist = {
                                        allPlaylistsViewModel.onPlaylistEvent(
                                            PlaylistEvent.SetSelectedPlaylist(
                                                it
                                            )
                                        )
                                        navigateToPlaylist(it.playlistId.toString())
                                    },
                                    playlistBottomSheetAction = {
                                        coroutineScope.launch {
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.SetSelectedPlaylist(
                                                    it
                                                )
                                            )
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    navigateToAlbum = navigateToAlbum,
                                    albumBottomSheetAction = { albumWithArtist ->
                                        coroutineScope.launch {
                                            selectedAlbumId = albumWithArtist.album.albumId
                                            allAlbumsViewModel.onAlbumEvent(
                                                AlbumEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    playMusicAction = { music ->
                                        coroutineScope.launch {
                                            playerViewManager.animateTo(
                                                newState = BottomSheetStates.EXPANDED,
                                            )
                                        }.invokeOnCompletion {
                                            val musicListSingleton = arrayListOf(music)
                                            playbackManager.setCurrentPlaylistAndMusic(
                                                music = music,
                                                musicList = musicListSingleton,
                                                isMainPlaylist = false,
                                                playlistId = null,
                                                isForcingNewPlaylist = true
                                            )
                                        }
                                    },
                                    musicBottomSheetAction = allMusicsViewModel::showMusicBottomSheet
                                )
                            }
                        )

                        ElementEnum.PLAYLISTS -> {
                            PagerScreen(
                                title = strings.playlists,
                                screen = {
                                    AllElementsComposable(
                                        retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                                        list = playlistState.playlists,
                                        title = strings.playlists,
                                        navigateToPlaylist = {
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.SetSelectedPlaylist(
                                                    it
                                                )
                                            )
                                            navigateToPlaylist(it.playlistId.toString())
                                        },
                                        playlistBottomSheetAction = {
                                            coroutineScope.launch {
                                                allPlaylistsViewModel.onPlaylistEvent(
                                                    PlaylistEvent.SetSelectedPlaylist(
                                                        it
                                                    )
                                                )
                                                allPlaylistsViewModel.onPlaylistEvent(
                                                    PlaylistEvent.BottomSheet(
                                                        isShown = true
                                                    )
                                                )
                                            }
                                        },
                                        createPlaylistComposable = {
                                            Icon(
                                                modifier = Modifier
                                                    .clickable {
                                                        allPlaylistsViewModel.onPlaylistEvent(
                                                            PlaylistEvent.CreatePlaylistDialog(
                                                                isShown = true
                                                            )
                                                        )
                                                    }
                                                    .size(UiConstants.ImageSize.medium),
                                                imageVector = Icons.Rounded.Add,
                                                contentDescription = strings.createPlaylistButton,
                                                tint = SoulSearchingColorTheme.colorScheme.onPrimary
                                            )
                                        },
                                        sortByName = {
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.SetSortType(SortType.NAME)
                                            )
                                        },
                                        sortByMostListenedAction = {
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.SetSortType(SortType.NB_PLAYED)
                                            )
                                        },
                                        sortByDateAction = {
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.SetSortType(SortType.ADDED_DATE)
                                            )
                                        },
                                        setSortDirectionAction = {
                                            val newDirection =
                                                if (playlistState.sortDirection == SortDirection.ASC) {
                                                    SortDirection.DESC
                                                } else {
                                                    SortDirection.ASC
                                                }
                                            allPlaylistsViewModel.onPlaylistEvent(
                                                PlaylistEvent.SetSortDirection(newDirection)
                                            )
                                        },
                                        sortType = playlistState.sortType,
                                        sortDirection = playlistState.sortDirection
                                    )
                                }
                            )
                        }

                        ElementEnum.ALBUMS -> PagerScreen(
                            title = strings.albums,
                            screen = {
                                AllElementsComposable(
                                    retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                                    list = albumState.albums,
                                    title = strings.albums,
                                    navigateToAlbum = navigateToAlbum,
                                    albumBottomSheetAction = { albumWithArtist ->
                                        coroutineScope.launch {
                                            selectedAlbumId = albumWithArtist.album.albumId
                                            allAlbumsViewModel.onAlbumEvent(
                                                AlbumEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    sortByName = {
                                        allAlbumsViewModel.onAlbumEvent(
                                            AlbumEvent.SetSortType(SortType.NAME)
                                        )
                                    },
                                    sortByMostListenedAction = {
                                        allAlbumsViewModel.onAlbumEvent(
                                            AlbumEvent.SetSortType(SortType.NB_PLAYED)
                                        )
                                    },
                                    sortByDateAction = {
                                        allAlbumsViewModel.onAlbumEvent(
                                            AlbumEvent.SetSortType(SortType.ADDED_DATE)
                                        )
                                    },
                                    setSortDirectionAction = {
                                        val newDirection =
                                            if (albumState.sortDirection == SortDirection.ASC) {
                                                SortDirection.DESC
                                            } else {
                                                SortDirection.ASC
                                            }
                                        allAlbumsViewModel.onAlbumEvent(
                                            AlbumEvent.SetSortDirection(newDirection)
                                        )
                                    },
                                    sortType = albumState.sortType,
                                    sortDirection = albumState.sortDirection
                                )
                            }
                        )

                        ElementEnum.ARTISTS -> PagerScreen(
                            title = strings.artists,
                            screen = {
                                AllElementsComposable(
                                    retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                                    list = artistState.artists,
                                    title = strings.artists,
                                    navigateToArtist = navigateToArtist,
                                    artistBottomSheetAction = {
                                        coroutineScope.launch {
                                            allArtistsViewModel.onArtistEvent(
                                                ArtistEvent.SetSelectedArtistWithMusics(
                                                    it
                                                )
                                            )
                                            allArtistsViewModel.onArtistEvent(
                                                ArtistEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    sortByName = {
                                        allArtistsViewModel.onArtistEvent(
                                            ArtistEvent.SetSortType(SortType.NAME)
                                        )
                                    },
                                    sortByMostListenedAction = {
                                        allArtistsViewModel.onArtistEvent(
                                            ArtistEvent.SetSortType(SortType.NB_PLAYED)
                                        )
                                    },
                                    sortByDateAction = {
                                        allArtistsViewModel.onArtistEvent(
                                            ArtistEvent.SetSortType(SortType.ADDED_DATE)
                                        )
                                    },
                                    setSortDirectionAction = {
                                        val newDirection =
                                            if (artistState.sortDirection == SortDirection.ASC) {
                                                SortDirection.DESC
                                            } else {
                                                SortDirection.ASC
                                            }
                                        allArtistsViewModel.onArtistEvent(
                                            ArtistEvent.SetSortDirection(newDirection)
                                        )
                                    },
                                    sortType = artistState.sortType,
                                    sortDirection = artistState.sortDirection
                                )
                            }
                        )

                        ElementEnum.MUSICS -> PagerScreen(
                            title = strings.musics,
                            screen = {
                                AllMusicsComposable(
                                    retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                                    musicState = musicState,
                                    sortByName = {
                                        allMusicsViewModel.onMusicEvent(
                                            MusicEvent.SetSortType(SortType.NAME)
                                        )

                                    },
                                    sortByDateAction = {
                                        allMusicsViewModel.onMusicEvent(
                                            MusicEvent.SetSortType(SortType.ADDED_DATE)
                                        )
                                    },
                                    sortByMostListenedAction = {
                                        allMusicsViewModel.onMusicEvent(
                                            MusicEvent.SetSortType(SortType.NB_PLAYED)
                                        )

                                    },
                                    setSortDirectionAction = {
                                        val newDirection =
                                            if (musicState.sortDirection == SortDirection.ASC) {
                                                SortDirection.DESC
                                            } else {
                                                SortDirection.ASC
                                            }
                                        allMusicsViewModel.onMusicEvent(
                                            MusicEvent.SetSortDirection(newDirection)
                                        )
                                    },
                                    onLongMusicClick = allMusicsViewModel::showMusicBottomSheet
                                )
                            }
                        )
                    }
                )
            }

            val pagerState = rememberPagerState(
                pageCount = { pagerScreens.size }
            )

            val currentPage by remember { derivedStateOf { pagerState.currentPage } }

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                // We only show the vertical shortcut if there is more than one panel to access.
                if (visibleElements.size > 1) {
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
                retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                musicState = musicState,
                albumState = albumState,
                artistState = artistState,
                playlistState = playlistState,
                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                onArtistEvent = allArtistsViewModel::onArtistEvent,
                navigateToPlaylist = navigateToPlaylist,
                navigateToArtist = navigateToArtist,
                navigateToAlbum = navigateToAlbum,
                isMainPlaylist = false,
                focusManager = focusManager,
                onSelectedMusicForBottomSheet = allMusicsViewModel::showMusicBottomSheet,
                onSelectedAlbumForBottomSheet = { album ->
                    selectedAlbumId = album.albumId
                    allAlbumsViewModel.onAlbumEvent(
                        AlbumEvent.BottomSheet(
                            isShown = true
                        )
                    )
                }
            )
        }
    }
}

