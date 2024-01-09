package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
actual fun MusicBottomSheet(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState: SheetState,
    musicState: MusicState,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: PlayerDraggableState,
    primaryColor: Color,
    textColor: Color
) {

}