package com.github.soulsearching.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.soulsearching.Constants
import com.github.soulsearching.strings
import com.github.soulsearching.types.SortDirection
import com.github.soulsearching.types.SortType
import java.util.UUID

@Composable
fun AllElementsComposable(
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    list: List<Any>,
    title: String,
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
    isUsingSort: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Constants.Spacing.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubMenuComposable(
                title = title,
                sortByDateAction = sortByDateAction,
                sortByMostListenedAction = sortByMostListenedAction,
                sortByName = sortByName,
                setSortDirectionAction = setSortDirectionAction,
                sortType = sortType,
                sortDirection = sortDirection,
                createPlaylistComposable = createPlaylistComposable,
                isUsingSort = isUsingSort
            )
        }
        if (list.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.FixedSize(Constants.ImageSize.veryLarge),
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