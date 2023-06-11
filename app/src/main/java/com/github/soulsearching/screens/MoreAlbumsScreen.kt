package com.github.soulsearching.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.github.soulsearching.composables.bottomSheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.viewModels.AllAlbumsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoreAlbumsScreen(
    allAlbumsViewModel: AllAlbumsViewModel,
    navigateToSelectedAlbum: (String) -> Unit,
    navigateToModifyAlbum: (String) -> Unit,
    finishAction: () -> Unit,
    coverList: ArrayList<ImageCover>
) {
    val coroutineScope = rememberCoroutineScope()

    val albumState by allAlbumsViewModel.state.collectAsState()

    AlbumBottomSheetEvents(
        albumState = albumState,
        onAlbumEvent = allAlbumsViewModel::onAlbumEvent,
        navigateToModifyAlbum = navigateToModifyAlbum
    )

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.albums),
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
                                newValue = SortType.NAME
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
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(Constants.ImageSize.huge),
                    contentPadding = PaddingValues(Constants.Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(albumState.albums) { albumWithArtist ->
                        BigPreviewComposable(
                            image = coverList.find { it.coverId == albumWithArtist.album.coverId }?.cover,
                            title = albumWithArtist.album.albumName,
                            text = if (albumWithArtist.artist != null) albumWithArtist.artist.artistName else "",
                            onClick = { navigateToSelectedAlbum(albumWithArtist.album.albumId.toString()) },
                            onLongClick = {
                                coroutineScope.launch {
                                    allAlbumsViewModel.onAlbumEvent(
                                        AlbumEvent.SetSelectedAlbum(
                                            albumWithArtist
                                        )
                                    )
                                    allAlbumsViewModel.onAlbumEvent(
                                        AlbumEvent.BottomSheet(
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
