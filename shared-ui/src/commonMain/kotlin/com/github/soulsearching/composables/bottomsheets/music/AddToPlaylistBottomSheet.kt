package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    selectedMusicId: UUID,
    onDismiss: () -> Unit,
    addToPlaylistModalSheetState: SheetState,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxSize(),
        onDismissRequest = onDismiss,
        sheetState = addToPlaylistModalSheetState,
        dragHandle = {}
    ) {
        AddToPlaylistMenuBottomSheet(
            primaryColor = primaryColor,
            textColor = textColor,
            playlistState = playlistState,
            onPlaylistEvent = onPlaylistsEvent,
            onDismiss = {
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!addToPlaylistModalSheetState.isVisible) onDismiss()
                    }
            },
            validationAction = {
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!addToPlaylistModalSheetState.isVisible) {
                            onPlaylistsEvent(PlaylistEvent.AddMusicToPlaylists(musicId = selectedMusicId))
                            onDismiss()
                        }
                    }
            }
        )
    }
}