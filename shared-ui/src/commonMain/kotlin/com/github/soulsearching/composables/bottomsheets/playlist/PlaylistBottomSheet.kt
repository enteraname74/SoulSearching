package com.github.soulsearching.composables.bottomsheets.playlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
expect fun PlaylistBottomSheet(
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    playlistModalSheetState: SheetState,
    navigateToModifyPlaylist: (String) -> Unit
)