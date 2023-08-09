package com.github.soulsearching.composables.bottomSheets.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    selectedMusicId: UUID,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState : SheetState,
    addToPlaylistModalSheetState : SheetState,
    playlistState : PlaylistState,
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
        sheetState = musicModalSheetState,
        dragHandle = {}
    ) {
        AddToPlaylistMenuBottomSheet(
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