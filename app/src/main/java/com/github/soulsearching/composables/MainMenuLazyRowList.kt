package com.github.soulsearching.composables

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.SelectedAlbumActivity
import com.github.soulsearching.SelectedArtistActivity
import com.github.soulsearching.SelectedPlaylistActivity
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithArtist
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.Playlist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainMenuLazyListRow(
    list : List<Any>,
    title : String,
    moreActivity : Class<Any>
) {

    val context = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SubMenuComposable(
            title = title,
            moreAction = {
                context.startActivity(
                    Intent(
                        context,
                        moreActivity
                    )
                )
            }
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
            items(list){element ->
                val modifier = Modifier.animateItemPlacement()
                when(element) {
                    is Playlist -> {
                        LazyRowComposable(
                            modifier = modifier,
                            image = element.playlistCover,
                            title = element.name,
                            text = "",
                            onClick = {
                                val intent = Intent(
                                    context,
                                    SelectedPlaylistActivity::class.java
                                )
                                intent.putExtra(
                                    "playlistId",
                                    element.playlistId.toString()
                                )
                                context.startActivity(intent)
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
                                val intent = Intent(
                                    context,
                                    SelectedAlbumActivity::class.java
                                )
                                intent.putExtra(
                                    "albumId",
                                    element.album.albumId.toString()
                                )
                                context.startActivity(intent)
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
                                val intent = Intent(
                                    context,
                                    SelectedArtistActivity::class.java
                                )
                                intent.putExtra(
                                    "artistId",
                                    element.artistId.toString()
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}