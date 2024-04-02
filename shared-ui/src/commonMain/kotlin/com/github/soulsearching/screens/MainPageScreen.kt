package com.github.soulsearching.screens

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.AllElementsComposable
import com.github.soulsearching.composables.AllMusicsComposable
import com.github.soulsearching.composables.MainMenuHeaderComposable
import com.github.soulsearching.composables.MainPageVerticalShortcut
import com.github.soulsearching.composables.bottomsheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetEvents
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.composables.dialog.CreatePlaylistDialog
import com.github.soulsearching.composables.search.SearchAll
import com.github.soulsearching.composables.search.SearchView
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.model.PagerScreen
import com.github.soulsearching.model.settings.ViewSettingsManager
import com.github.soulsearching.states.AlbumState
import com.github.soulsearching.states.ArtistState
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.states.QuickAccessState
import com.github.soulsearching.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.ElementEnum
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainPageScreen(
    allMusicsViewModel: AllMusicsViewModel,
    allPlaylistsViewModel: AllPlaylistsViewModel,
    allAlbumsViewModel: AllAlbumsViewModel,
    allArtistsViewModel: AllArtistsViewModel,
    allImageCoversViewModel: AllImageCoversViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
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
    musicState: MusicState,
    playlistState: PlaylistState,
    albumState: AlbumState,
    artistState: ArtistState,
    quickAccessState: QuickAccessState,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()
    MusicBottomSheetEvents(
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
        onPlaylistsEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
        navigateToModifyMusic = navigateToModifyMusic,
        playerMusicListViewModel = playerMusicListViewModel,
        playerDraggableState = playerDraggableState
    )

    PlaylistBottomSheetEvents(
        playlistState = playlistState,
        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
        navigateToModifyPlaylist = navigateToModifyPlaylist
    )

    AlbumBottomSheetEvents(
        albumState = albumState,
        onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent,
        navigateToModifyAlbum = navigateToModifyAlbum
    )

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
                            tween(Constants.AnimationDuration.normal)
                        )
                    }
                }
            )

            val visibleElements by rememberSaveable {
                mutableStateOf(viewSettingsManager.getListOfVisibleElements())
            }
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
                                    albumBottomSheetAction = {
                                        coroutineScope.launch {
                                            allAlbumsViewModel.handler.onAlbumEvent(
                                                AlbumEvent.SetSelectedAlbum(
                                                    it
                                                )
                                            )
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
                                                tween(Constants.AnimationDuration.normal)
                                            )
                                        }.invokeOnCompletion {
                                            val musicListSingleton = arrayListOf(music)
                                            if (!PlayerUtils.playerViewModel.handler.isSameMusic(
                                                    music.musicId
                                                )
                                            ) {
                                                playerMusicListViewModel.handler.savePlayerMusicList(
                                                    musicListSingleton.map { it.musicId }
                                                )
                                            }
                                            PlayerUtils.playerViewModel.handler.setCurrentPlaylistAndMusic(
                                                music = music,
                                                playlist = musicListSingleton,
                                                isMainPlaylist = false,
                                                playlistId = null,
                                                isForcingNewPlaylist = true
                                            )
                                        }
                                    },
                                    musicBottomSheetAction = {
                                        coroutineScope.launch {
                                            allMusicsViewModel.handler.onMusicEvent(
                                                MusicEvent.SetSelectedMusic(
                                                    it
                                                )
                                            )
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
                                                    .size(Constants.ImageSize.medium),
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
                                    albumBottomSheetAction = {
                                        coroutineScope.launch {
                                            allAlbumsViewModel.handler.onAlbumEvent(
                                                AlbumEvent.SetSelectedAlbum(
                                                    it
                                                )
                                            )
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
                                    allMusicsViewModel = allMusicsViewModel,
                                    retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                                    musicState = musicState,
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
                                    sortByName = {
                                        allMusicsViewModel.handler.onMusicEvent(
                                            MusicEvent.SetSortType(SortType.NAME)
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
                                    sortType = musicState.sortType,
                                    sortDirection = musicState.sortDirection,
                                    savePlayerMusicListMethod = {
                                        playerMusicListViewModel.handler.savePlayerMusicList(it)
                                    }
                                )
                            }
                        )
                    }
                )
            }

            val currentEnumPage = allMusicsViewModel.handler.getCurrentPage(
                visibleElements = visibleElements
            )

            val pagerState = rememberPagerState(
                initialPage = currentEnumPage,
                pageCount = { pagerScreens.size }
            )

            val currentPage by remember { derivedStateOf { pagerState.currentPage } }

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

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
            maxHeight = maxHeight
        ) { searchText, focusManager ->
            SearchAll(
                searchText = searchText,
                retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                musicState = musicState,
                albumState = albumState,
                artistState = artistState,
                playlistState = playlistState,
                onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
                onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
                onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
                onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent,
                navigateToPlaylist = navigateToPlaylist,
                navigateToArtist = navigateToArtist,
                navigateToAlbum = navigateToAlbum,
                playerMusicListViewModel = playerMusicListViewModel,
                playerDraggableState = playerDraggableState,
                isMainPlaylist = false,
                focusManager = focusManager
            )
        }
    }
}