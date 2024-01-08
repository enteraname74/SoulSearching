package com.github.soulsearching.screens

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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.composables.SortOptionsComposable
import com.github.soulsearching.composables.bottomsheet.artist.ArtistBottomSheetEvents
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoreArtistsScreen(
    allArtistsViewModel: AllArtistsViewModel,
    navigateToSelectedArtist: (String) -> Unit,
    navigateToModifyArtist: (String) -> Unit,
    finishAction: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?
) {
    val coroutineScope = rememberCoroutineScope()

    val artistState by allArtistsViewModel.state.collectAsState()

    ArtistBottomSheetEvents(
        artistState = artistState,
        onArtistEvent = allArtistsViewModel::onArtistEvent,
        navigateToModifyArtist = navigateToModifyArtist
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.artists),
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
                setSortDirection = {
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
                items = artistState.artists,
            ) { artistWithMusics ->
                BigPreviewComposable(
                    modifier = Modifier.animateItemPlacement(),
                    image = retrieveCoverMethod(artistWithMusics.artist.coverId),
                    title = artistWithMusics.artist.artistName,
                    text = if (artistWithMusics.musics.size == 1) {
                        stringResource(id = R.string.one_music)
                    } else {
                        stringResource(
                            id = R.string.multiple_musics,
                            artistWithMusics.musics.size
                        )
                    },
                    onClick = {
                        navigateToSelectedArtist(artistWithMusics.artist.artistId.toString())
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            allArtistsViewModel.onArtistEvent(
                                ArtistEvent.SetSelectedArtistWithMusics(
                                    artistWithMusics
                                )
                            )
                            allArtistsViewModel.onArtistEvent(
                                ArtistEvent.BottomSheet(
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