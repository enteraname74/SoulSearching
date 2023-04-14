package com.github.soulsearching.composables.tabLayoutScreens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.soulsearching.SelectedAlbumActivity
import com.github.soulsearching.composables.GridPlaylistComposable
import com.github.soulsearching.states.AlbumWithArtistState

@Composable
fun AlbumsScreen(
    state: AlbumWithArtistState
) {
    val context = LocalContext.current
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary),
        columns = GridCells.Adaptive(196.dp)
    ) {
        items(state.albums) { albumWithArtist ->
            GridPlaylistComposable(
                image = albumWithArtist.album.albumCover,
                title = albumWithArtist.album.albumName,
                text = albumWithArtist.artist!!.artistName,
                onClick = {

                    val intent = Intent(context, SelectedAlbumActivity::class.java)
                    intent.putExtra(
                        "albumId",
                        albumWithArtist.album.albumId.toString()
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}