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
import com.github.soulsearching.SelectedArtistActivity
import com.github.soulsearching.composables.GridPlaylistComposable
import com.github.soulsearching.states.ArtistState

@Composable
fun ArtistsScreen(
    state: ArtistState
) {
    val context = LocalContext.current
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary),
        columns = GridCells.Adaptive(196.dp)
    ) {
        items(state.artists) { artist ->
            GridPlaylistComposable(
                image = artist.artistCover,
                title = artist.artistName,
                text = "small talk...",
                onClick = {
                    val intent = Intent(context, SelectedArtistActivity::class.java)
                    intent.putExtra(
                        "artistId",
                        artist.artistId.toString()
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}