package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.theme.DynamicColor
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AddToPlaylistBottomSheet(
    selectedMusicId: UUID,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    addToPlaylistModalSheetState: SheetState,
    playlistState: PlaylistState,
    primaryColor: Color,
    textColor: Color
) {

}