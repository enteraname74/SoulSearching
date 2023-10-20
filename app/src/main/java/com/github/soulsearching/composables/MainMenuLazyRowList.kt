package com.github.soulsearching.composables

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.database.model.*
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.ui.theme.DynamicColor
import java.util.*

@Composable
fun MainMenuLazyListRow(
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    list: List<Any>,
    title: String,
    navigateToMore: () -> Unit,
    navigateToPlaylist: (Playlist) -> Unit = {},
    navigateToAlbum: (String) -> Unit = {},
    navigateToArtist: (String) -> Unit = {},
    playMusicAction: (Music) -> Unit = {},
    musicBottomSheetAction: (Music) -> Unit = {},
    playlistBottomSheetAction: (Playlist) -> Unit = {},
    albumBottomSheetAction: (AlbumWithArtist) -> Unit = {},
    artistBottomSheetAction: (ArtistWithMusics) -> Unit = {},
    createPlaylistComposable: @Composable (() -> Unit) = {},
    sortByName: () -> Unit = {},
    sortByDateAction: () -> Unit = {},
    sortByMostListenedAction: () -> Unit = {},
    setSortDirectionAction: () -> Unit = {},
    sortType: Int = SortType.NAME,
    sortDirection: Int = SortDirection.DESC,
    isUsingSort: Boolean = true,
    isUsingMoreButton: Boolean = true
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
            setSortDirectionAction = setSortDirectionAction,
            rightComposable = {
                if (isUsingMoreButton) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = navigateToMore) {
                            Text(
                                text = stringResource(id = R.string.more),
                                color = DynamicColor.onPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            },
            sortType = sortType,
            sortDirection = sortDirection,
            createPlaylistComposable = createPlaylistComposable,
            isUsingSort = isUsingSort
        )
        if (list.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                contentPadding = PaddingValues(
                    start = Constants.Spacing.medium,
                    end = Constants.Spacing.medium
                )
            ) {
                items(
                    items = list
                ) { element ->
                    when (element) {
                        is PlaylistWithMusicsNumber -> {
                            BigPreviewComposable(
                                image = element.playlist.coverId?.let { retrieveCoverMethod(it) },
                                title = element.playlist.name,
                                text = if (element.musicsNumber == 1) {
                                    stringResource(id = R.string.one_music)
                                } else stringResource(
                                    id = R.string.multiple_musics, element.musicsNumber
                                ),
                                onClick = {
                                    navigateToPlaylist(element.playlist)
                                },
                                onLongClick = { playlistBottomSheetAction(element.playlist) },
                                isFavoritePlaylist = element.playlist.isFavorite
                            )
                        }
                        is AlbumWithArtist -> {
                            BigPreviewComposable(
                                image = element.album.coverId?.let { retrieveCoverMethod(it) },
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
                                image = element.artist.coverId?.let { retrieveCoverMethod(it) },
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
                        is Music -> {
                            BigPreviewComposable(
                                image = retrieveCoverMethod(element.coverId),
                                title = element.name,
                                text = element.artist,
                                onClick = { playMusicAction(element) },
                                onLongClick = { musicBottomSheetAction(element) }
                            )
                        }
                    }
                }
            }
        } else {
            NoElementView()
        }
    }
}