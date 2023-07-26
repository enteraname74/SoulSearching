package com.github.soulsearching.screens

import android.graphics.Bitmap
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
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.SortDirection
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.BigPreviewComposable
import com.github.soulsearching.composables.SortOptionsComposable
import com.github.soulsearching.composables.bottomSheets.artist.ArtistBottomSheetEvents
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.viewModels.AllArtistsViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoreArtistsScreen(
    allArtistsViewModel: AllArtistsViewModel,
    navigateToSelectedArtist: (String) -> Unit,
    navigateToModifyArtist: (String) -> Unit,
    finishAction: () -> Unit,
    retrieveCoverMethod: (UUID?) -> Bitmap?
) {
    val coroutineScope = rememberCoroutineScope()

    val artistState by allArtistsViewModel.state.collectAsState()

    ArtistBottomSheetEvents(
        artistState = artistState,
        onArtistEvent = allArtistsViewModel::onArtistEvent,
        navigateToModifyArtist = navigateToModifyArtist
    )

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.artists),
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
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(Constants.ImageSize.huge),
                    contentPadding = PaddingValues(Constants.Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    item(key = "0") {
                        BigPreviewComposable(
                            image = retrieveCoverMethod(artistState.artists[0].artist.coverId),
                            title = artistState.artists[0].artist.artistName,
                            text = if (artistState.artists[0].musics.size == 1) {
                                stringResource(id = R.string.one_music)
                            } else {
                                stringResource(
                                    id = R.string.multiple_musics,
                                    artistState.artists[0].musics.size
                                )
                            },
                            onClick = {
                                navigateToSelectedArtist(artistState.artists[0].artist.artistId.toString())
                            },
                            onLongClick = {
                                coroutineScope.launch {
                                    allArtistsViewModel.onArtistEvent(
                                        ArtistEvent.SetSelectedArtist(
                                            artistState.artists[0]
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
                    items(
                        items = artistState.artists.subList(1, artistState.artists.size),
                        key = { artist -> artist.artist.artistId }) { artistWithMusics ->
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
                                        ArtistEvent.SetSelectedArtist(
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
    )
}