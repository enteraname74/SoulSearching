package com.github.soulsearching.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.composables.SortOptionsComposable
import com.github.soulsearching.composables.bottomSheets.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.viewModels.AllPlaylistsViewModel
import kotlinx.coroutines.launch

@Composable
fun MorePlaylistsScreen(
    allPlaylistsViewModel: AllPlaylistsViewModel,
    navigateToSelectedPlaylist: (String) -> Unit,
    finishAction: () -> Unit,
    navigateToModifyPlaylist: (String) -> Unit,
    coverList: ArrayList<ImageCover>
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
                    .background(color = MaterialTheme.colorScheme.secondary)
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
                            val newDirection = if (playlistState.sortDirection == SortDirection.ASC) {
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = MaterialTheme.colorScheme.secondary),
                    columns = GridCells.Adaptive(196.dp)
                ) {

                    items(playlistState.playlists) { playlistWithMusics ->
                        BigPreviewComposable(
                            image = coverList.find { it.coverId == playlistWithMusics.playlist.coverId }?.cover,
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