package com.github.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.soulsearching.events.PlaylistEvent

@Composable
expect fun CreatePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit
)