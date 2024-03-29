package com.github.soulsearching.screens

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.composables.SortOptionsComposable
import com.github.soulsearching.composables.bottomsheet.playlist.PlaylistBottomSheetEvents
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import kotlinx.coroutines.launch
import java.util.UUID

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DynamicColor.primary)
            .padding(bottom = 80.dp)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.playlists),
            leftAction = finishAction
        )
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
                setSortDirection = {
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
        LazyVerticalGrid(
            columns = GridCells.FixedSize(Constants.ImageSize.huge),
            contentPadding = PaddingValues(
                top = Constants.Spacing.small,
                start = Constants.Spacing.small,
                end = Constants.Spacing.small,
                bottom = 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(
                items = playlistState.playlists
            ) { playlistWithMusics ->
                BigPreviewComposable(
                    modifier = Modifier.animateItemPlacement(),
                    image = retrieveCoverMethod(playlistWithMusics.playlist.coverId),
                    title = playlistWithMusics.playlist.name,
                    text = if (playlistWithMusics.musicsNumber == 1) {
                        stringResource(id = R.string.one_music)
                    } else {
                        stringResource(
                            id = R.string.multiple_musics,
                            playlistWithMusics.musicsNumber
                        )
                    },
                    onClick = {
                        allPlaylistsViewModel.onPlaylistEvent(
                            PlaylistEvent.SetSelectedPlaylist(
                                playlistWithMusics.playlist
                            )
                        )
                        navigateToSelectedPlaylist(playlistWithMusics.playlist.playlistId.toString())
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            allPlaylistsViewModel.onPlaylistEvent(
                                PlaylistEvent.SetSelectedPlaylist(
                                    playlistWithMusics.playlist
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