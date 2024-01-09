package com.github.soulsearching.screens

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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.composables.SortOptionsComposable
import com.github.soulsearching.composables.bottomsheets.album.AlbumBottomSheetEvents
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MoreAlbumsScreen(
    allAlbumsViewModel: AllAlbumsViewModel,
    navigateToSelectedAlbum: (String) -> Unit,
    navigateToModifyAlbum: (String) -> Unit,
    finishAction: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?
) {
    val coroutineScope = rememberCoroutineScope()

    val albumState by allAlbumsViewModel.handler.state.collectAsState()

    AlbumBottomSheetEvents(
        albumState = albumState,
        onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent,
        navigateToModifyAlbum = navigateToModifyAlbum
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.albums),
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
                setSortDirection = {
                    val newDirection = if (albumState.sortDirection == SortDirection.ASC) {
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
                items = albumState.albums
            ) { albumWithArtist ->
                BigPreviewComposable(
                    image = retrieveCoverMethod(albumWithArtist.album.coverId),
                    title = albumWithArtist.album.albumName,
                    text = if (albumWithArtist.artist != null) albumWithArtist.artist!!.artistName else "",
                    onClick = { navigateToSelectedAlbum(albumWithArtist.album.albumId.toString()) },
                    onLongClick = {
                        coroutineScope.launch {
                            allAlbumsViewModel.handler.onAlbumEvent(
                                AlbumEvent.SetSelectedAlbum(
                                    albumWithArtist
                                )
                            )
                            allAlbumsViewModel.handler.onAlbumEvent(
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
