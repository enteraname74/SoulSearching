package com.github.soulsearching.composables

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.ModifyPlaylistActivity
import com.github.soulsearching.R
import com.github.soulsearching.SelectedPlaylistActivity
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.PlaylistEvent

@Composable
fun PlaylistItemComposable(
    playlist: Playlist,
    onClick : () -> Unit
) {
    val activity = LocalContext.current as Activity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(Constants.Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
    ) {
        AppImage(bitmap = playlist.playlistCover, size = 55.dp)
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = playlist.name,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "test2",
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}