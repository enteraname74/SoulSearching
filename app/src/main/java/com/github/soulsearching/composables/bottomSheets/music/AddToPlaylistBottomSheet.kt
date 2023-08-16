package com.github.soulsearching.composables.bottomSheets.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    selectedMusicId: UUID,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    addToPlaylistModalSheetState: SheetState,
    playlistState: PlaylistState,
    primaryColor: Color = DynamicColor.secondary,
    textColor: Color = DynamicColor.onSecondary
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = {
            onMusicEvent(
                MusicEvent.AddToPlaylistBottomSheet(
                    isShown = false
                )
            )
        },
        sheetState = addToPlaylistModalSheetState,
        dragHandle = {}
    ) {
        AddToPlaylistMenuBottomSheet(
            primaryColor = primaryColor,
            textColor = textColor,
            playlistState = playlistState,
            onPlaylistEvent = onPlaylistsEvent,
            cancelAction = {
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!addToPlaylistModalSheetState.isVisible) {
                            onMusicEvent(
                                MusicEvent.AddToPlaylistBottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
            },
            validationAction = {
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!addToPlaylistModalSheetState.isVisible) {
                            onPlaylistsEvent(PlaylistEvent.AddMusicToPlaylists(musicId = selectedMusicId))
                            onMusicEvent(
                                MusicEvent.AddToPlaylistBottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
            }
        )
    }
}