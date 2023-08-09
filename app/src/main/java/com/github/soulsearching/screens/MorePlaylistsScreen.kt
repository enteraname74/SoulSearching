package com.github.soulsearching.screens

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.composables.SortOptionsComposable
import com.github.soulsearching.composables.bottomSheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.AllPlaylistsViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MorePlaylistsScreen(
    allPlaylistsViewModel: AllPlaylistsViewModel,
    navigateToSelectedPlaylist: (String) -> Unit,
    finishAction: () -> Unit,
    navigateToModifyPlaylist: (String) -> Unit,
    retrieveCoverMethod: (UUID?) -> Bitmap?
) {
    val coroutineScope = rememberCoroutineScope()

    val playlistState by allPlaylistsViewModel.state.collectAsState()

    PlaylistBottomSheetEvents(
        playlistState = playlistState,
        onPlaylistEvent = allPlaylistsViewModel::onPlaylistEvent,
        navigateToModifyPlaylist = navigateToModifyPlaylist
    )

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.playlists),
                leftAction = finishAction
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = DynamicColor.secondary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Constants.Spacing.medium),
                    horizontalArrangement = Arrangement.End
                ) {
                    SortOptionsComposable(
                        imageSize = Constants.Spacing.large,
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
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(Constants.ImageSize.huge),
                    contentPadding = PaddingValues(Constants.Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    item(key = "0") {
                        BigPreviewComposable(
                            image = retrieveCoverMethod(playlistState.playlists[0].playlist.coverId),
                            title = playlistState.playlists[0].playlist.name,
                            text = if (playlistState.playlists[0].musics.size == 1) {
                                stringResource(id = R.string.one_music)
                            } else {
                                stringResource(
                                    id = R.string.multiple_musics,
                                    playlistState.playlists[0].musics.size
                                )
                            },
                            onClick = {
                                navigateToSelectedPlaylist(playlistState.playlists[0].playlist.playlistId.toString())
                            },
                            onLongClick = {
                                coroutineScope.launch {
                                    allPlaylistsViewModel.onPlaylistEvent(
                                        PlaylistEvent.SetSelectedPlaylist(
                                            playlistState.playlists[0]
                                        )
                                    )
                                    allPlaylistsViewModel.onPlaylistEvent(
                                        PlaylistEvent.BottomSheet(
                                            isShown = true
                                        )
                                    )
                                }
                            },
                            imageSize = Constants.ImageSize.huge
                        )
                    }
                    items(
                        items = playlistState.playlists.subList(1, playlistState.playlists.size),
                        key = { playlist -> playlist.playlist.playlistId }
                    ) { playlistWithMusics ->
                        BigPreviewComposable(
                            modifier = Modifier.animateItemPlacement(),
                            image = retrieveCoverMethod(playlistWithMusics.playlist.coverId),
                            title = playlistWithMusics.playlist.name,
                            text = if (playlistWithMusics.musics.size == 1) {
                                stringResource(id = R.string.one_music)
                            } else {
                                stringResource(
                                    id = R.string.multiple_musics,
                                    playlistWithMusics.musics.size
                                )
                            },
                            onClick = {
                                navigateToSelectedPlaylist(playlistWithMusics.playlist.playlistId.toString())
                            },
                            onLongClick = {
                                coroutineScope.launch {
                                    allPlaylistsViewModel.onPlaylistEvent(
                                        PlaylistEvent.SetSelectedPlaylist(
                                            playlistWithMusics
                                        )
                                    )
                                    allPlaylistsViewModel.onPlaylistEvent(
                                        PlaylistEvent.BottomSheet(
                                            isShown = true
                                        )
                                    )
                                }
                            },
                            imageSize = Constants.ImageSize.huge
                        )
                    }
                }
            }
        }
    )
}