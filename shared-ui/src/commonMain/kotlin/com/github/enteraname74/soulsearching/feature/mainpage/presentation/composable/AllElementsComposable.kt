package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun AllElementsComposable(
    list: List<Any>,
    title: String,
    navigateToPlaylist: (playlistId: String) -> Unit = {},
    navigateToAlbum: (albumId: String) -> Unit = {},
    navigateToArtist: (artistId: String) -> Unit = {},
    playMusicAction: (Music) -> Unit = {},
    musicBottomSheetAction: (Music) -> Unit = {},
    playlistBottomSheetAction: (Playlist) -> Unit = {},
    albumBottomSheetAction: (Album) -> Unit = {},
    artistBottomSheetAction: (ArtistWithMusics) -> Unit = {},
    createPlaylistComposable: @Composable () -> Unit = {},
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
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
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
                columns = GridCells.FixedSize(UiConstants.ImageSize.veryLarge),
                contentPadding = PaddingValues(
                    top = UiConstants.Spacing.small,
                    start = UiConstants.Spacing.small,
                    end = UiConstants.Spacing.small,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(
                    items = list
                ) { element ->
                    when (element) {
                        is PlaylistWithMusicsNumber -> {
                            BigPreviewComposable(
                                coverId = element.playlist.coverId,
                                title = element.playlist.name,
                                text = strings.musics(element.musicsNumber),
                                onClick = {
                                    navigateToPlaylist(element.playlist.playlistId.toString())
                                },
                                onLongClick = { playlistBottomSheetAction(element.playlist) },
                                isFavoritePlaylist = element.playlist.isFavorite
                            )
                        }
                        is AlbumWithMusics -> {
                            BigPreviewComposable(
                                coverId = element.album.coverId,
                                title = element.album.albumName,
                                text = if (element.artist != null) element.artist!!.artistName else "",
                                onClick = {
                                    navigateToAlbum(element.album.albumId.toString())
                                          },
                                onLongClick = { albumBottomSheetAction(element.album) }
                            )
                        }
                        is ArtistWithMusics -> {
                            BigPreviewComposable(
                                coverId = element.artist.coverId,
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
                                coverId = element.coverId,
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