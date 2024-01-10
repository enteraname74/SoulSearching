package com.github.soulsearching.composables.bottomsheets.playlist

import androidx.compose.runtime.Composable
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState


@Composable
actual fun PlaylistBottomSheetEvents(
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyPlaylist : (String) -> Unit
) {

}