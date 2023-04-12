package com.github.soulsearching.composables.tabLayoutScreens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.soulsearching.SelectedPlaylistActivity
import com.github.soulsearching.composables.PlaylistItemComposable
import com.github.soulsearching.states.PlaylistState

@Composable
fun PlaylistsScreen(
    state: PlaylistState
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.playlists) { playlist ->
            PlaylistItemComposable(playlist = playlist, onClick = {
                val intent = Intent(context, SelectedPlaylistActivity::class.java)
                intent.putExtra(
                    "playlistId",
                    playlist.playlistId.toString()
                )
                context.startActivity(intent)
            })
        }
    }
}