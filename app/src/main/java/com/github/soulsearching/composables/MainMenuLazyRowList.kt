package com.github.soulsearching.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.database.model.AlbumWithArtist
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.Playlist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainMenuLazyListRow(
    list: List<Any>,
    title: String,
    navigateToMore: () -> Unit,
    navigateToPlaylist: (String) -> Unit = {},
    navigateToAlbum: (String) -> Unit = {},
    navigateToArtist: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SubMenuComposable(
            title = title,
            moreAction = navigateToMore
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
                    is Playlist -> {
                        LazyRowComposable(
                            modifier = modifier,
                            image = element.playlistCover,
                            title = element.name,
                            text = "",
                            onClick = {
                                navigateToPlaylist(element.playlistId.toString())
                            }
                        )
                    }
                    is AlbumWithArtist -> {
                        LazyRowComposable(
                            modifier = modifier,
                            image = element.album.albumCover,
                            title = element.album.albumName,
                            text = if (element.artist != null) element.artist.artistName else "",
                            onClick = {
                                navigateToAlbum(element.album.albumId.toString())
                            }
                        )
                    }
                    is Artist -> {
                        LazyRowComposable(
                            modifier = modifier,
                            image = element.artistCover,
                            title = element.artistName,
                            text = "",
                            onClick = {
                                navigateToArtist(element.artistId.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}