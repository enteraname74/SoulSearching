package com.github.soulsearching.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.*
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.artist.ArtistBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.composables.dialogs.CreatePlaylistDialog
import com.github.soulsearching.composables.searchComposables.SearchAll
import com.github.soulsearching.composables.searchComposables.SearchView
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.*
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.*
import kotlinx.coroutines.launch
import java.util.*

@Suppress("UNCHECKED_CAST")
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
    navigateToMorePlaylist: () -> Unit,
    navigateToMoreArtists: () -> Unit,
    navigateToMoreShortcuts: () -> Unit,
    navigateToMoreAlbums: () -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateToModifyPlaylist: (String) -> Unit,
    navigateToModifyAlbum: (String) -> Unit,
    navigateToModifyArtist: (String) -> Unit,
    navigateToSettings: () -> Unit,
    playerSwipeableState: SwipeableState<BottomSheetStates>,
    searchSwipeableState: SwipeableState<BottomSheetStates>,
    musicState: MusicState,
    playlistState: PlaylistState,
    albumState: AlbumState,
    artistState: ArtistState,
    quickAccessState: QuickAccessState
) {
    val coroutineScope = rememberCoroutineScope()
    MusicBottomSheetEvents(
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = allMusicsViewModel::onMusicEvent,
        onPlaylistsEvent = allPlaylistsViewModel::onPlaylistEvent,
        navigateToModifyMusic = navigateToModifyMusic,
        playerMusicListViewModel = playerMusicListViewModel,
        playerSwipeableState = playerSwipeableState
    )

    PlaylistBottomSheetEvents(
        playlistState = playlistState,
        onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
        navigateToModifyPlaylist = navigateToModifyPlaylist
    )

    AlbumBottomSheetEvents(
        albumState = albumState,
        onAlbumEvent = allAlbumsViewModel::onAlbumEvent,
        navigateToModifyAlbum = navigateToModifyAlbum
    )

    ArtistBottomSheetEvents(
        artistState = artistState,
        onArtistEvent = allArtistsViewModel::onArtistEvent,
        navigateToModifyArtist = navigateToModifyArtist
    )

    if (playlistState.isCreatePlaylistDialogShown) {
        CreatePlaylistDialog(onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent)
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MainMenuHeaderComposable(
                settingsAction = navigateToSettings,
                searchAction = {
                    coroutineScope.launch {
                        searchSwipeableState.animateTo(
                            BottomSheetStates.EXPANDED,
                            tween(Constants.AnimationTime.normal)
                        )
                    }
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                        list = quickAccessState.allQuickAccess,
                        title = stringResource(id = R.string.quick_access),
                        navigateToMore = navigateToMoreShortcuts,
                        isUsingSort = false,
                        isUsingMoreButton = false,
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
                            navigateToPlaylist(it)
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
                        albumBottomSheetAction = {
                            coroutineScope.launch {
                                allAlbumsViewModel.onAlbumEvent(
                                    AlbumEvent.SetSelectedAlbum(
                                        it
                                    )
                                )
                                allAlbumsViewModel.onAlbumEvent(
                                    AlbumEvent.BottomSheet(
                                        isShown = true
                                    )
                                )
                            }
                        },
                        playMusicAction = { music ->
                            coroutineScope.launch {
                                playerSwipeableState.animateTo(BottomSheetStates.EXPANDED)
                            }.invokeOnCompletion {
                                val musicListSingleton = arrayListOf(music)
                                if (!PlayerUtils.playerViewModel.isSameMusic(music.musicId)) {
                                    playerMusicListViewModel.savePlayerMusicList(musicListSingleton)
                                }
                                PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
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
                                allMusicsViewModel.onMusicEvent(
                                    MusicEvent.SetSelectedMusic(
                                        it
                                    )
                                )
                                allMusicsViewModel.onMusicEvent(
                                    MusicEvent.BottomSheet(
                                        isShown = true
                                    )
                                )
                            }
                        }
                    )
                }
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                        list = playlistState.playlists,
                        title = stringResource(id = R.string.playlists),
                        navigateToMore = navigateToMorePlaylist,
                        navigateToPlaylist = {
                            navigateToPlaylist(it)
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
                                    .size(Constants.ImageSize.medium),
                                imageVector = Icons.Rounded.Add,
                                contentDescription = stringResource(id = R.string.create_playlist_button),
                                tint = DynamicColor.onPrimary
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
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                        list = albumState.albums,
                        title = stringResource(id = R.string.albums),
                        navigateToMore = navigateToMoreAlbums,
                        navigateToAlbum = navigateToAlbum,
                        albumBottomSheetAction = {
                            coroutineScope.launch {
                                allAlbumsViewModel.onAlbumEvent(
                                    AlbumEvent.SetSelectedAlbum(
                                        it
                                    )
                                )
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
                            val newDirection = if (albumState.sortDirection == SortDirection.ASC) {
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
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                        list = artistState.artists,
                        title = stringResource(id = R.string.artists),
                        navigateToMore = navigateToMoreArtists,
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
                            val newDirection = if (artistState.sortDirection == SortDirection.ASC) {
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
                stickyHeader {
                    SubMenuComposable(
                        title = stringResource(id = R.string.musics),
                        backgroundColor = DynamicColor.primary,
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
                        sortByName = {
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.SetSortType(SortType.NAME)
                            )

                        },
                        setSortDirectionAction = {
                            val newDirection = if (musicState.sortDirection == SortDirection.ASC) {
                                SortDirection.DESC
                            } else {
                                SortDirection.ASC
                            }
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.SetSortDirection(newDirection)
                            )
                        },
                        rightComposable = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = Constants.Spacing.large)
                                    .size(Constants.ImageSize.medium)
                                    .clickable {
                                        coroutineScope
                                            .launch {
                                                playerSwipeableState.animateTo(BottomSheetStates.EXPANDED)
                                            }
                                            .invokeOnCompletion {
                                                PlayerUtils.playerViewModel.playShuffle(musicState.musics)
                                                playerMusicListViewModel.savePlayerMusicList(
                                                    PlayerUtils.playerViewModel.currentPlaylist
                                                )
                                            }
                                    },
                                imageVector = Icons.Rounded.Shuffle,
                                contentDescription = stringResource(id = R.string.shuffle_button_desc),
                                tint = DynamicColor.onPrimary
                            )
                        },
                        sortType = musicState.sortType,
                        sortDirection = musicState.sortDirection
                    )
                }
                items(items = musicState.musics) { elt ->
                    MusicItemComposable(
                        music = elt,
                        onClick = { music ->
                            coroutineScope.launch {
                                playerSwipeableState.animateTo(BottomSheetStates.EXPANDED)
                            }.invokeOnCompletion {
                                if (!PlayerUtils.playerViewModel.isSamePlaylist(true, null)) {
                                    playerMusicListViewModel.savePlayerMusicList(musicState.musics)
                                }
                                PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                                    music = music,
                                    playlist = musicState.musics,
                                    isMainPlaylist = true,
                                    playlistId = null,
                                )
                            }
                        },
                        onLongClick = {
                            coroutineScope.launch {
                                allMusicsViewModel.onMusicEvent(
                                    MusicEvent.SetSelectedMusic(
                                        elt
                                    )
                                )
                                allMusicsViewModel.onMusicEvent(
                                    MusicEvent.BottomSheet(
                                        isShown = true
                                    )
                                )
                            }
                        },
                        musicCover = allImageCoversViewModel.getImageCover(elt.coverId),
//                            recoverMethod = allImageCoversViewModel::getCover
                    )

                }
            }
        }
        SearchView(
            swipeableState = searchSwipeableState,
            maxHeight = maxHeight,
            placeholder = stringResource(id = R.string.search_all),
            playerSwipeableState = playerSwipeableState
        ) { searchText, focusManager ->
            SearchAll(
                searchText = searchText,
                retrieveCoverMethod = allImageCoversViewModel::getImageCover,
                musicState = musicState,
                albumState = albumState,
                artistState = artistState,
                playlistState = playlistState,
                onMusicEvent = allMusicsViewModel::onMusicEvent,
                onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
                onArtistEvent = allArtistsViewModel::onArtistEvent,
                onAlbumEvent = allAlbumsViewModel::onAlbumEvent,
                navigateToPlaylist = navigateToPlaylist,
                navigateToArtist = navigateToArtist,
                navigateToAlbum = navigateToAlbum,
                playerMusicListViewModel = playerMusicListViewModel,
                playerSwipeableState = playerSwipeableState,
                isMainPlaylist = false,
                focusManager = focusManager
            )
        }
    }
}