package com.github.soulsearching.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.soulsearching.Constants
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import java.util.UUID

@Composable
fun MainMenuLazyListRow(
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
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
                                text = strings.more,
                                color = SoulSearchingColorTheme.colorScheme.onPrimary,
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
                                text = strings.musics(element.musicsNumber),
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
                                text = if (element.artist != null) element.artist!!.artistName else "",
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
                                text = strings.musics(element.musics.size),
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