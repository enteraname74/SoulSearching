package com.github.soulsearching.composables.bottomSheets

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.ModifyMusicActivity
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBottomSheet(
    onMusicEvent : (MusicEvent) -> Unit,
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    musicModalSheetState : SheetState,
    musicState : MusicState
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity

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
                        }
                    }

                val intent =
                    Intent(context, ModifyMusicActivity::class.java)
                intent.putExtra(
                    "musicId",
                    musicState.selectedMusic.musicId.toString()
                )
                context.startActivity(intent)
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