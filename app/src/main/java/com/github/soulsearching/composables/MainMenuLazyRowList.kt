package com.github.soulsearching.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.database.model.AlbumWithArtist
import com.github.soulsearching.database.model.ArtistWithMusics
import com.github.soulsearching.database.model.PlaylistWithMusics

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainMenuLazyListRow(
    list: List<Any>,
    title: String,
    navigateToMore: () -> Unit,
    navigateToPlaylist: (String) -> Unit = {},
    navigateToAlbum: (String) -> Unit = {},
    navigateToArtist: (String) -> Unit = {},
    playlistBottomSheetAction: (PlaylistWithMusics) -> Unit = {},
    albumBottomSheetAction: (AlbumWithArtist) -> Unit = {},
    artistBottomSheetAction: (ArtistWithMusics) -> Unit = {},
    createPlaylistComposable: @Composable (() -> Unit) = {},
    sortByName : () -> Unit,
    sortByDateAction : () -> Unit,
    sortByMostListenedAction : () -> Unit,
    setSortTypeAction : () -> Unit,
    sortType : Int,
    sortDirection : Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SubMenuComposable(
            title = title,
            sortByDateAction = sortByDateAction,
            sortByMostListenedAction = sortByMostListenedAction,
            sortByName = sortByName,
            setSortTypeAction = setSortTypeAction,
            rightComposable = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = navigateToMore) {
                        Text(
                            text = stringResource(id = R.string.more),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            },
            sortType = sortType,
            sortDirection = sortDirection,
            createPlaylistComposable = createPlaylistComposable
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Constants.Spacing.medium,
                    end = Constants.Spacing.medium
                ),
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            items(list) { element ->
                val modifier = Modifier.animateItemPlacement()
                when (element) {
                    is PlaylistWithMusics -> {
                        BigPreviewComposable(
                            modifier = modifier,
                            image = element.playlist.playlistCover,
                            title = element.playlist.name,
                            text = if (element.musics.size == 1) {
                                stringResource(id = R.string.one_music)
                            } else stringResource(
                                id = R.string.multiple_musics, element.musics.size
                            ),
                            onClick = {
                                navigateToPlaylist(element.playlist.playlistId.toString())
                            },
                            onLongClick = { playlistBottomSheetAction(element) }
                        )
                    }
                    is AlbumWithArtist -> {
                        BigPreviewComposable(
                            modifier = modifier,
                            image = element.album.albumCover,
                            title = element.album.albumName,
                            text = if (element.artist != null) element.artist.artistName else "",
                            onClick = {
                                navigateToAlbum(element.album.albumId.toString())
                            },
                            onLongClick = { albumBottomSheetAction(element) }
                        )
                    }
                    is ArtistWithMusics -> {
                        BigPreviewComposable(
                            modifier = modifier,
                            image = element.artist.artistCover,
                            title = element.artist.artistName,
                            text = if (element.musics.size == 1) {
                                stringResource(id = R.string.one_music)
                            } else stringResource(
                                id = R.string.multiple_musics, element.musics.size
                            ),
                            onClick = {
                                navigateToArtist(element.artist.artistId.toString())
                            },
                            onLongClick = { artistBottomSheetAction(element) }
                        )
                    }
                }
            }
        }
    }
}