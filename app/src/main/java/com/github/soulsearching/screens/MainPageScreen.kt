package com.github.soulsearching.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.artist.ArtistBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.composables.dialogs.CreatePlaylistDialog
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.viewModels.AllAlbumsViewModel
import com.github.soulsearching.viewModels.AllArtistsViewModel
import com.github.soulsearching.viewModels.AllMusicsViewModel
import com.github.soulsearching.viewModels.AllPlaylistsViewModel
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPageScreen(
    allMusicsViewModel: AllMusicsViewModel,
    allPlaylistsViewModel: AllPlaylistsViewModel,
    allAlbumsViewModel: AllAlbumsViewModel,
    allArtistsViewModel: AllArtistsViewModel,
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
    navigateToModifyArtist: (String) -> Unit
) {
    val musicState by allMusicsViewModel.state.collectAsState()
    val playlistState by allPlaylistsViewModel.state.collectAsState()
    val albumState by allAlbumsViewModel.state.collectAsState()
    val artistState by allArtistsViewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    MusicBottomSheetEvents(
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = allMusicsViewModel::onMusicEvent,
        onPlaylistsEvent = allPlaylistsViewModel::onPlaylistEvent,
        navigateToModifyMusic = navigateToModifyMusic
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

    Scaffold(
        topBar = { MainMenuHeaderComposable() },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(paddingValues)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        SubMenuComposable(
                            title = stringResource(id = R.string.shortcuts),
                            sortByDateAction = {},
                            sortByMostListenedAction = {},
                            sortByName = {},
                            setSortTypeAction = {},
                            rightComposable = {
                                TextButton(onClick = {}) {
                                    Text(
                                        text = stringResource(id = R.string.more),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        )
                        TestButtons(
                            onMusicEvent = allMusicsViewModel::onMusicEvent,
                            onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent
                        )
                    }
                }
                item {
                    MainMenuLazyListRow(
                        list = playlistState.playlists,
                        title = stringResource(id = R.string.playlists),
                        navigateToMore = navigateToMorePlaylist,
                        navigateToPlaylist = navigateToPlaylist,
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
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.create_playlist_button),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        sortByName = { allPlaylistsViewModel.onPlaylistEvent(PlaylistEvent.SetSortType(SortType.NAME)) },
                        sortByMostListenedAction = { allPlaylistsViewModel.onPlaylistEvent(PlaylistEvent.SetSortType(SortType.NB_PLAYED)) },
                        sortByDateAction = { allPlaylistsViewModel.onPlaylistEvent(PlaylistEvent.SetSortType(SortType.ADDED_DATE)) },
                        setSortTypeAction = { allPlaylistsViewModel.onPlaylistEvent(PlaylistEvent.SetDirectionSort) }
                    )
                }
                item {
                    MainMenuLazyListRow(
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
                        sortByName = { allAlbumsViewModel.onAlbumEvent(AlbumEvent.SetSortType(SortType.NAME)) },
                        sortByMostListenedAction = { allAlbumsViewModel.onAlbumEvent(AlbumEvent.SetSortType(SortType.NB_PLAYED)) },
                        sortByDateAction = { allAlbumsViewModel.onAlbumEvent(AlbumEvent.SetSortType(SortType.ADDED_DATE)) },
                        setSortTypeAction = { allAlbumsViewModel.onAlbumEvent(AlbumEvent.SetDirectionSort) }
                    )
                }
                item {
                    MainMenuLazyListRow(
                        list = artistState.artists,
                        title = stringResource(id = R.string.artists),
                        navigateToMore = navigateToMoreArtists,
                        navigateToArtist = navigateToArtist,
                        artistBottomSheetAction = {
                            coroutineScope.launch {
                                allArtistsViewModel.onArtistEvent(
                                    ArtistEvent.SetSelectedArtist(
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
                        sortByName = { allArtistsViewModel.onArtistEvent(ArtistEvent.SetSortType(SortType.NAME)) },
                        sortByMostListenedAction = { allArtistsViewModel.onArtistEvent(ArtistEvent.SetSortType(SortType.NB_PLAYED)) },
                        sortByDateAction = { allArtistsViewModel.onArtistEvent(ArtistEvent.SetSortType(SortType.ADDED_DATE)) },
                        setSortTypeAction = { allArtistsViewModel.onArtistEvent(ArtistEvent.SetDirectionSort) }
                    )
                }
                stickyHeader {
                    SubMenuComposable(
                        title = stringResource(id = R.string.musics),
                        sortByDateAction = {
                            Log.d("SORT", "SORT")
                            allMusicsViewModel.onMusicEvent(MusicEvent.SetSortType(SortType.ADDED_DATE))
                        },
                        sortByMostListenedAction = { allMusicsViewModel.onMusicEvent(MusicEvent.SetSortType(SortType.NB_PLAYED)) },
                        sortByName = { allMusicsViewModel.onMusicEvent(MusicEvent.SetSortType(SortType.ADDED_DATE)) },
                        setSortTypeAction = { allMusicsViewModel.onMusicEvent(MusicEvent.SetDirectionSort) },
                        rightComposable = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = Constants.Spacing.large)
                                    .size(Constants.ImageSize.medium)
                                    .clickable {},
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(id = R.string.shuffle_button_desc),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                }
                items(musicState.musics) { music ->
                    Row(Modifier.animateItemPlacement()) {
                        MusicItemComposable(
                            music = music,
                            onClick = allMusicsViewModel::onMusicEvent,
                            onLongClick = {
                                coroutineScope.launch {
                                    allMusicsViewModel.onMusicEvent(
                                        MusicEvent.SetSelectedMusic(
                                            music
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
                }
            }
        }
    )
}