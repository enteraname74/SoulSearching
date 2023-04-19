package com.github.soulsearching.composables

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent

@Composable
fun TestButtons(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit
) {
    val context = LocalContext.current as Activity

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        item {
            Button(onClick = {
                onMusicEvent(MusicEvent.AddMusic)
            }) {
                Text("Test ajout simple")
            }
        }

        item {
            Button(onClick = {
            }) {
                Text("Test ajout playlist fav")
            }
        }

        item {
            Button(onClick = { /*TODO*/ }) {
                Text("Test ajout last play")
            }
        }
    }
}