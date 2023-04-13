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
import com.github.soulsearching.states.AlbumState

@Composable
fun AlbumsScreen(
    state: AlbumState
) {
    val context = LocalContext.current
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary),
        columns = GridCells.Adaptive(196.dp)
    ) {
        items(state.albums) { album ->
            GridPlaylistComposable(
                image = album.albumCover,
                title = album.name,
                text = album.artist,
                onClick = {

                    val intent = Intent(context, SelectedAlbumActivity::class.java)
                    intent.putExtra(
                        "albumId",
                        album.albumId.toString()
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}