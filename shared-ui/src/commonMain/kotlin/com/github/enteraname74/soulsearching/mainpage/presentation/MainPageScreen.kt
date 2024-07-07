package com.github.soulsearching.mainpage.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.soulsearching.composables.bottomsheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetEvents
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.events.AlbumEvent
import com.github.soulsearching.domain.events.ArtistEvent
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.domain.viewmodel.AllArtistsViewModel
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.soulsearching.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.soulsearching.elementpage.playlistpage.presentation.SelectedPlaylistScreen
import com.github.soulsearching.mainpage.domain.model.ElementEnum
import com.github.soulsearching.mainpage.domain.model.PagerScreen
import com.github.soulsearching.mainpage.domain.model.SortDirection
import com.github.soulsearching.mainpage.domain.model.SortType
import com.github.soulsearching.mainpage.domain.state.AlbumState
import com.github.soulsearching.mainpage.domain.state.ArtistState
import com.github.soulsearching.mainpage.domain.state.MainPageState
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.mainpage.domain.state.QuickAccessState
import com.github.soulsearching.mainpage.presentation.composable.AllElementsComposable
import com.github.soulsearching.mainpage.presentation.composable.AllMusicsComposable
import com.github.soulsearching.mainpage.presentation.composable.CreatePlaylistDialog
import com.github.soulsearching.mainpage.presentation.composable.MainMenuHeaderComposable
import com.github.soulsearching.mainpage.presentation.composable.MainPageVerticalShortcut
import com.github.soulsearching.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.soulsearching.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.soulsearching.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.search.presentation.SearchAll
import com.github.soulsearching.search.presentation.SearchView
import com.github.soulsearching.settings.domain.ViewSettingsManager
import com.github.soulsearching.settings.presentation.SettingsScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlinx.coroutines.launch
import java.util.UUID

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
        val playerViewModel = getScreenModel<PlayerViewModel>()

        val musicState by allMusicsViewModel.handler.state.collectAsState()
        val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
        val albumState by allAlbumsViewModel.handler.state.collectAsState()
        val artistState by allArtistsViewModel.handler.state.collectAsState()
        val quickAccessState by allQuickAccessViewModel.handler.state.collectAsState()

        val searchDraggableState = allMusicsViewModel.handler.searchDraggableState
        val playerDraggableState = playerViewModel.handler.playerDraggableState

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
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            navigateToModifyPlaylist = { playlistId ->
                navigator.push(
                    ModifyPlaylistScreen(
                        selectedPlaylistId = playlistId
                    )
                )
            },
            navigateToModifyAlbum = { albumId ->
                navigator.push(
                    ModifyAlbumScreen(
                        selectedAlbumId = albumId
                    )
                )
            },
            navigateToModifyArtist = { artistId ->
                navigator.push(
                    ModifyArtistScreen(
                        selectedArtistId = artistId
                    )
                )
            },
            navigateToSettings = {
                navigator.push(
                    SettingsScreen()
                )
            },
            playerDraggableState = playerDraggableState,
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
    navigateToModifyMusic: (String) -> Unit,
    navigateToModifyPlaylist: (String) -> Unit,
    navigateToModifyAlbum: (String) -> Unit,
    navigateToModifyArtist: (String) -> Unit,
    navigateToSettings: () -> Unit,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    searchDraggableState: SwipeableState<BottomSheetStates>,
    musicState: MainPageState,
    playlistState: PlaylistState,
    albumState: AlbumState,
    artistState: ArtistState,
    quickAccessState: QuickAccessState,
    viewSettingsManager: ViewSettingsManager = injectElement(),
    playbackManager: PlaybackManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedMusicId by rememberSaveable {
        mutableStateOf<UUID?>(null)
    }

    var selectedAlbumId by rememberSaveable {
        mutableStateOf<UUID?>(null)
    }

    musicState.musics.find { it.musicId == selectedMusicId }?.let { music ->
        MusicBottomSheetEvents(
            selectedMusic = music,
            playlistsWithMusics = musicState.allPlaylists,
            navigateToModifyMusic = navigateToModifyMusic,
            playerDraggableState = playerDraggableState,
            isDeleteMusicDialogShown = musicState.isDeleteDialogShown,
            isBottomSheetShown = musicState.isBottomSheetShown,
            isAddToPlaylistBottomSheetShown = musicState.isAddToPlaylistBottomSheetShown,
            isRemoveFromPlaylistDialogShown = musicState.isRemoveFromPlaylistDialogShown,
            onDismiss = {
                allMusicsViewModel.handler.onMusicEvent(
                    MusicEvent.BottomSheet(isShown = false)
                )
            },
            onSetDeleteMusicDialogVisibility = { isShown ->
                allMusicsViewModel.handler.onMusicEvent(
                    MusicEvent.DeleteDialog(isShown = isShown)
                )
            },
            onSetAddToPlaylistBottomSheetVisibility = { isShown ->
                allMusicsViewModel.handler.onMusicEvent(
                    MusicEvent.AddToPlaylistBottomSheet(isShown = isShown)
                )
            },
            onDeleteMusic = {
                allMusicsViewModel.handler.onMusicEvent(
                    MusicEvent.DeleteMusic(musicId = music.musicId)
                )
            },
            onToggleQuickAccessState = {
                allMusicsViewModel.handler.onMusicEvent(
                    MusicEvent.ToggleQuickAccessState(musicId = music.musicId)
                )
            },
            onAddMusicToSelectedPlaylists = { selectedPlaylistsIds ->
                allPlaylistsViewModel.handler.onPlaylistEvent(
                    PlaylistEvent.AddMusicToPlaylists(
                        musicId = music.musicId,
                        selectedPlaylistsIds = selectedPlaylistsIds
                    )
                )
            },
            retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover
        )
    }

    PlaylistBottomSheetEvents(
        playlistState = playlistState,
        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
        navigateToModifyPlaylist = navigateToModifyPlaylist
    )

    albumState.albums.find { it.album.albumId == selectedAlbumId }?.let { albumWithArtist ->
        AlbumBottomSheetEvents(
            selectedAlbum = albumWithArtist.album,
            navigateToModifyAlbum = navigateToModifyAlbum,
            isDeleteAlbumDialogShown = albumState.isDeleteDialogShown,
            isBottomSheetShown = albumState.isBottomSheetShown,
            onDeleteAlbum = {
                allAlbumsViewModel.handler.onAlbumEvent(
                    AlbumEvent.DeleteAlbum(albumId = albumWithArtist.album.albumId)
                )
            },
            onDismissBottomSheet = {
                allAlbumsViewModel.handler.onAlbumEvent(
                    AlbumEvent.BottomSheet(isShown = false)
                )
            },
            onSetDeleteAlbumDialogVisibility = { isShown ->
                allAlbumsViewModel.handler.onAlbumEvent(
                    AlbumEvent.DeleteDialog(isShown = isShown)
                )
            },
            onToggleQuickAccessState = {
                allAlbumsViewModel.handler.onAlbumEvent(
                    AlbumEvent.UpdateQuickAccessState(album = albumWithArtist.album)
                )
            }
        )

    }
    ArtistBottomSheetEvents(
        artistState = artistState,
        onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
        navigateToModifyArtist = navigateToModifyArtist
    )

    if (playlistState.isCreatePlaylistDialogShown) {
        CreatePlaylistDialog(onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent)
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
                                    retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                                    list = quickAccessState.allQuickAccess,
                                    title = strings.quickAccess,
                                    isUsingSort = false,
                                    navigateToArtist = navigateToArtist,
                                    artistBottomSheetAction = {
                                        coroutineScope.launch {
                                            allArtistsViewModel.handler.onArtistEvent(
                                                ArtistEvent.SetSelectedArtistWithMusics(
                                                    it
                                                )
                                            )
                                            allArtistsViewModel.handler.onArtistEvent(
                                                ArtistEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    navigateToPlaylist = {
                                        allPlaylistsViewModel.handler.onPlaylistEvent(
                                            PlaylistEvent.SetSelectedPlaylist(
                                                it
                                            )
                                        )
                                        navigateToPlaylist(it.playlistId.toString())
                                    },
                                    playlistBottomSheetAction = {
                                        coroutineScope.launch {
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
                                                PlaylistEvent.SetSelectedPlaylist(
                                                    it
                                                )
                                            )
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
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
                                            allAlbumsViewModel.handler.onAlbumEvent(
                                                AlbumEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    playMusicAction = { music ->
                                        coroutineScope.launch {
                                            playerDraggableState.animateTo(
                                                BottomSheetStates.EXPANDED,
                                                tween(UiConstants.AnimationDuration.normal)
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
                                    musicBottomSheetAction = { music ->
                                        coroutineScope.launch {
                                            selectedMusicId = music.musicId
                                            allMusicsViewModel.handler.onMusicEvent(
                                                MusicEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        )

                        ElementEnum.PLAYLISTS -> {
                            PagerScreen(
                                title = strings.playlists,
                                screen = {
                                    AllElementsComposable(
                                        retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                                        list = playlistState.playlists,
                                        title = strings.playlists,
                                        navigateToPlaylist = {
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
                                                PlaylistEvent.SetSelectedPlaylist(
                                                    it
                                                )
                                            )
                                            navigateToPlaylist(it.playlistId.toString())
                                        },
                                        playlistBottomSheetAction = {
                                            coroutineScope.launch {
                                                allPlaylistsViewModel.handler.onPlaylistEvent(
                                                    PlaylistEvent.SetSelectedPlaylist(
                                                        it
                                                    )
                                                )
                                                allPlaylistsViewModel.handler.onPlaylistEvent(
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
                                                        allPlaylistsViewModel.handler.onPlaylistEvent(
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
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
                                                PlaylistEvent.SetSortType(SortType.NAME)
                                            )
                                        },
                                        sortByMostListenedAction = {
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
                                                PlaylistEvent.SetSortType(SortType.NB_PLAYED)
                                            )
                                        },
                                        sortByDateAction = {
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
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
                                            allPlaylistsViewModel.handler.onPlaylistEvent(
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
                                    retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                                    list = albumState.albums,
                                    title = strings.albums,
                                    navigateToAlbum = navigateToAlbum,
                                    albumBottomSheetAction = { albumWithArtist ->
                                        coroutineScope.launch {
                                            selectedAlbumId = albumWithArtist.album.albumId
                                            allAlbumsViewModel.handler.onAlbumEvent(
                                                AlbumEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    sortByName = {
                                        allAlbumsViewModel.handler.onAlbumEvent(
                                            AlbumEvent.SetSortType(SortType.NAME)
                                        )
                                    },
                                    sortByMostListenedAction = {
                                        allAlbumsViewModel.handler.onAlbumEvent(
                                            AlbumEvent.SetSortType(SortType.NB_PLAYED)
                                        )
                                    },
                                    sortByDateAction = {
                                        allAlbumsViewModel.handler.onAlbumEvent(
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
                                        allAlbumsViewModel.handler.onAlbumEvent(
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
                                    retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                                    list = artistState.artists,
                                    title = strings.artists,
                                    navigateToArtist = navigateToArtist,
                                    artistBottomSheetAction = {
                                        coroutineScope.launch {
                                            allArtistsViewModel.handler.onArtistEvent(
                                                ArtistEvent.SetSelectedArtistWithMusics(
                                                    it
                                                )
                                            )
                                            allArtistsViewModel.handler.onArtistEvent(
                                                ArtistEvent.BottomSheet(
                                                    isShown = true
                                                )
                                            )
                                        }
                                    },
                                    sortByName = {
                                        allArtistsViewModel.handler.onArtistEvent(
                                            ArtistEvent.SetSortType(SortType.NAME)
                                        )
                                    },
                                    sortByMostListenedAction = {
                                        allArtistsViewModel.handler.onArtistEvent(
                                            ArtistEvent.SetSortType(SortType.NB_PLAYED)
                                        )
                                    },
                                    sortByDateAction = {
                                        allArtistsViewModel.handler.onArtistEvent(
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
                                        allArtistsViewModel.handler.onArtistEvent(
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
                                    retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                                    musicState = musicState,
                                    sortByName = {
                                        allMusicsViewModel.handler.onMusicEvent(
                                            MusicEvent.SetSortType(SortType.NAME)
                                        )

                                    },
                                    sortByDateAction = {
                                        allMusicsViewModel.handler.onMusicEvent(
                                            MusicEvent.SetSortType(SortType.ADDED_DATE)
                                        )
                                    },
                                    sortByMostListenedAction = {
                                        allMusicsViewModel.handler.onMusicEvent(
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
                                        allMusicsViewModel.handler.onMusicEvent(
                                            MusicEvent.SetSortDirection(newDirection)
                                        )
                                    },
                                    playerDraggableState = playerDraggableState,
                                    onLongMusicClick = { music ->
                                        selectedMusicId = music.musicId
                                        allMusicsViewModel.handler.onMusicEvent(
                                            MusicEvent.BottomSheet(
                                                isShown = true
                                            )
                                        )
                                    }
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
                                   allMusicsViewModel.handler.currentPage = visibleElements[it]
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
            playerDraggableState = playerDraggableState,
            placeholder = strings.searchAll,
            maxHeight = maxHeight,
            focusRequester = searchBarFocusRequester
        ) { searchText, focusManager ->
            SearchAll(
                searchText = searchText,
                retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                musicState = musicState,
                albumState = albumState,
                artistState = artistState,
                playlistState = playlistState,
                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
                navigateToPlaylist = navigateToPlaylist,
                navigateToArtist = navigateToArtist,
                navigateToAlbum = navigateToAlbum,
                playerDraggableState = playerDraggableState,
                isMainPlaylist = false,
                focusManager = focusManager,
                onSelectedMusicForBottomSheet = { music ->
                    coroutineScope.launch {
                        selectedMusicId = music.musicId
                        allMusicsViewModel.handler.onMusicEvent(
                            MusicEvent.BottomSheet(
                                isShown = true
                            )
                        )
                    }
                },
                onSelectedAlbumForBottomSheet = { album ->
                    selectedAlbumId = album.albumId
                    allAlbumsViewModel.handler.onAlbumEvent(
                        AlbumEvent.BottomSheet(
                            isShown = true
                        )
                    )
                }
            )
        }
    }
}

