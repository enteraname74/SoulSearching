package com.github.soulsearching.mainpage.presentation.composable

import androidx.compose.runtime.Composable
import com.github.soulsearching.domain.events.PlaylistEvent

@Composable
expect fun CreatePlaylistDialog(
    onPlaylistEvent: (PlaylistEvent) -> Unit
)