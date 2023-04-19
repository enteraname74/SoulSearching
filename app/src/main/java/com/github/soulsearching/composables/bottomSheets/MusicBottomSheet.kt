package com.github.soulsearching.composables.bottomSheets

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBottomSheet(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState: SheetState,
    musicState: MusicState,
    navigateToModifyMusic : (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onMusicEvent(
                MusicEvent.BottomSheet(
                    isShown = false
                )
            )
        },
        sheetState = musicModalSheetState,
        dragHandle = {}
    ) {
        MusicBottomSheetMenu(
            modifyAction = {
                coroutineScope.launch { musicModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) {
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                            navigateToModifyMusic(musicState.selectedMusic.musicId.toString())
                        }
                    }
            },
            removeAction = {
                onMusicEvent(MusicEvent.DeleteDialog(isShown = true))
            },
            addToPlaylistAction = {
                coroutineScope.launch { musicModalSheetState.hide() }.invokeOnCompletion {
                    if (!musicModalSheetState.isVisible) {
                        onMusicEvent(
                            MusicEvent.BottomSheet(
                                isShown = false
                            )
                        )
                        onPlaylistEvent(
                            PlaylistEvent.PlaylistsSelection(
                                musicId = musicState.selectedMusic.musicId
                            )
                        )
                        onMusicEvent(
                            MusicEvent.AddToPlaylistBottomSheet(
                                isShown = true
                            )
                        )
                    }
                }
            }
        )
    }
}