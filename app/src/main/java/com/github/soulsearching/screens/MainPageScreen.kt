package com.github.soulsearching.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.*
import com.github.soulsearching.composables.*
import com.github.soulsearching.composables.bottomSheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.artist.ArtistBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.bottomSheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.composables.dialogs.CreatePlaylistDialog
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.viewModels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainPageScreen(
    allMusicsViewModel: AllMusicsViewModel,
    allPlaylistsViewModel: AllPlaylistsViewModel,
    allAlbumsViewModel: AllAlbumsViewModel,
    allArtistsViewModel: AllArtistsViewModel,
    allImageCoversViewModel: AllImageCoversViewModel,
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
    swipeableState: SwipeableState<BottomSheetStates>
) {
    val musicState by allMusicsViewModel.state.collectAsState()
    val playlistState by allPlaylistsViewModel.state.collectAsState()
    val albumState by allAlbumsViewModel.state.collectAsState()
    val artistState by allArtistsViewModel.state.collectAsState()
    val imageCovers by allImageCoversViewModel.state.collectAsState()

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

    var cleanImagesLaunched by rememberSaveable {
        mutableStateOf(false)
    }


    if (imageCovers.covers.isNotEmpty() && !cleanImagesLaunched) {
        LaunchedEffect(key1 = "Launch") {
            Log.d("LAUNCHED EFFECT", "")

            CoroutineScope(Dispatchers.IO).launch {
                for (cover in imageCovers.covers) {
                    allImageCoversViewModel.verifyIfImageIsUsed(cover)
                }
                cleanImagesLaunched = true
            }
        }
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
                            },
                            sortType = 0,
                            sortDirection = 0
                        )
                        TestButtons(
                            addMusic = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    allMusicsViewModel.addMusic(
                                        musicToAdd = Music(
                                            name = "TEST",
                                            artist = "TEST",
                                            album = "TEST"
                                        ),
                                        musicCover = null
                                    )
                                }
                            }
                        )
                    }
                }
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) },
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
                        sortByName = {
                            allPlaylistsViewModel.onPlaylistEvent(
                                PlaylistEvent.SetSortType(
                                    SortType.NAME
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_TYPE_KEY,
                                newValue = SortType.NAME
                            )
                        },
                        sortByMostListenedAction = {
                            allPlaylistsViewModel.onPlaylistEvent(
                                PlaylistEvent.SetSortType(SortType.NB_PLAYED)
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_TYPE_KEY,
                                newValue = SortType.NB_PLAYED
                            )
                        },
                        sortByDateAction = {
                            allPlaylistsViewModel.onPlaylistEvent(
                                PlaylistEvent.SetSortType(
                                    SortType.ADDED_DATE
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_TYPE_KEY,
                                newValue = SortType.ADDED_DATE
                            )
                        },
                        setSortTypeAction = {
                            val newDirection =
                                if (playlistState.sortDirection == SortDirection.ASC) {
                                    SortDirection.DESC
                                } else {
                                    SortDirection.ASC
                                }
                            allPlaylistsViewModel.onPlaylistEvent(
                                PlaylistEvent.SetSortDirection(
                                    newDirection
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_DIRECTION_KEY,
                                newValue = newDirection
                            )
                        },
                        sortType = playlistState.sortType,
                        sortDirection = playlistState.sortDirection
                    )
                }
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) },
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
                                AlbumEvent.SetSortType(
                                    SortType.NAME
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ALBUMS_TYPE_KEY,
                                newValue = SortType.NAME
                            )
                        },
                        sortByMostListenedAction = {
                            allAlbumsViewModel.onAlbumEvent(
                                AlbumEvent.SetSortType(
                                    SortType.NB_PLAYED
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ALBUMS_TYPE_KEY,
                                newValue = SortType.NB_PLAYED
                            )
                        },
                        sortByDateAction = {
                            allAlbumsViewModel.onAlbumEvent(
                                AlbumEvent.SetSortType(
                                    SortType.ADDED_DATE
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ALBUMS_TYPE_KEY,
                                newValue = SortType.ADDED_DATE
                            )
                        },
                        setSortTypeAction = {
                            val newDirection = if (albumState.sortDirection == SortDirection.ASC) {
                                SortDirection.DESC
                            } else {
                                SortDirection.ASC
                            }
                            Log.d("NEW UPDATE", "NEW UPDATE")
                            allAlbumsViewModel.onAlbumEvent(
                                AlbumEvent.SetSortDirection(
                                    newDirection
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ALBUMS_DIRECTION_KEY,
                                newValue = newDirection
                            )
                        },
                        sortType = albumState.sortType,
                        sortDirection = albumState.sortDirection
                    )
                }
                item {
                    MainMenuLazyListRow(
                        retrieveCoverMethod = { allImageCoversViewModel.getImageCover(it) },
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
                        sortByName = {
                            allArtistsViewModel.onArtistEvent(
                                ArtistEvent.SetSortType(
                                    SortType.NAME
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ARTISTS_TYPE_KEY,
                                newValue = SortType.NAME
                            )
                        },
                        sortByMostListenedAction = {
                            allArtistsViewModel.onArtistEvent(
                                ArtistEvent.SetSortType(
                                    SortType.NB_PLAYED
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ARTISTS_TYPE_KEY,
                                newValue = SortType.NB_PLAYED
                            )
                        },
                        sortByDateAction = {
                            allArtistsViewModel.onArtistEvent(
                                ArtistEvent.SetSortType(
                                    SortType.ADDED_DATE
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ARTISTS_TYPE_KEY,
                                newValue = SortType.ADDED_DATE
                            )
                        },
                        setSortTypeAction = {
                            val newDirection = if (artistState.sortDirection == SortDirection.ASC) {
                                SortDirection.DESC
                            } else {
                                SortDirection.ASC
                            }
                            allArtistsViewModel.onArtistEvent(
                                ArtistEvent.SetSortDirection(
                                    newDirection
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_ARTISTS_DIRECTION_KEY,
                                newValue = newDirection
                            )
                        },
                        sortType = artistState.sortType,
                        sortDirection = artistState.sortDirection
                    )
                }
                stickyHeader {
                    SubMenuComposable(
                        title = stringResource(id = R.string.musics),
                        sortByDateAction = {
                            allMusicsViewModel.onMusicEvent(MusicEvent.SetSortType(SortType.ADDED_DATE))
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_MUSICS_TYPE_KEY,
                                newValue = SortType.ADDED_DATE
                            )
                        },
                        sortByMostListenedAction = {
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.SetSortType(
                                    SortType.NB_PLAYED
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_MUSICS_TYPE_KEY,
                                newValue = SortType.NB_PLAYED
                            )
                        },
                        sortByName = {
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.SetSortType(
                                    SortType.NAME
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_MUSICS_TYPE_KEY,
                                newValue = SortType.NAME
                            )
                        },
                        setSortTypeAction = {
                            val newDirection = if (musicState.sortDirection == SortDirection.ASC) {
                                SortDirection.DESC
                            } else {
                                SortDirection.ASC
                            }
                            allMusicsViewModel.onMusicEvent(
                                MusicEvent.SetSortDirection(
                                    newDirection
                                )
                            )
                            SharedPrefUtils.updateSort(
                                keyToUpdate = SharedPrefUtils.SORT_MUSICS_DIRECTION_KEY,
                                newValue = newDirection
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
                                                swipeableState.animateTo(BottomSheetStates.EXPANDED)
                                            }
                                            .invokeOnCompletion {
                                                PlayerUtils.playerViewModel.playShuffle(musicState.musics)
                                            }
                                    },
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(id = R.string.shuffle_button_desc),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        sortType = musicState.sortType,
                        sortDirection = musicState.sortDirection
                    )
                }
                items(items = musicState.musics, key = { music -> music.musicId }) { music ->
                    Row(Modifier.animateItemPlacement()) {
                        MusicItemComposable(
                            music = music,
                            onClick = { music ->
                                coroutineScope.launch {
                                    swipeableState.animateTo(BottomSheetStates.EXPANDED)
                                }.invokeOnCompletion {
                                    PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                                        music = music,
                                        playlist = musicState.musics,
                                        isMainPlaylist = true,
                                        playlistId = null,
                                        bitmap = allImageCoversViewModel.getImageCover(music.coverId)
                                    )
                                }
                            },
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
                            },
                            musicCover = allImageCoversViewModel.getImageCover(music.coverId)
                        )
                    }
                }
            }
        }
    )
}