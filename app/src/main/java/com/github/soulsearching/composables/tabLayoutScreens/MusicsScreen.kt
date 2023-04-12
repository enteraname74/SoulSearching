package com.github.soulsearching.composables.tabLayoutScreens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.Constants
import com.github.soulsearching.ModifyMusicActivity
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.bottomSheets.MusicFileBottomSheet
import com.github.soulsearching.composables.dialogs.DeleteMusicDialog
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicsScreen(
    state: MusicState,
    onEvent: (MusicEvent) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState()

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    if (state.isDeleteDialogShown) {
        DeleteMusicDialog(
            onMusicEvent = onEvent,
            confirmAction = {
                coroutineScope.launch { modalSheetState.hide() }.invokeOnCompletion {
                    if (!modalSheetState.isVisible) {
                        onEvent(MusicEvent.BottomSheet(isShown = false))
                    }
                }
            }
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(start = Constants.Spacing.medium, end = Constants.Spacing.medium)
    ) {
        items(state.musics) { music ->
            MusicItemComposable(
                music = music,
                onClick = onEvent,
                onLongClick = {
                    coroutineScope.launch {
                        onEvent(MusicEvent.SetSelectedMusic(music))
                        onEvent(MusicEvent.BottomSheet(isShown = true))
                    }
                }
            )
        }
    }

    if (state.isBottomSheetShown) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(MusicEvent.BottomSheet(isShown = false)) },
            sheetState = modalSheetState,
            dragHandle = {}
        ) {
            MusicFileBottomSheet(
                modifyAction = {
                    coroutineScope.launch { modalSheetState.hide() }.invokeOnCompletion {
                        if (!modalSheetState.isVisible) {
                            onEvent(MusicEvent.BottomSheet(isShown = false))
                        }
                    }

                    val intent = Intent(context, ModifyMusicActivity::class.java)
                    intent.putExtra(
                        "musicId",
                        state.selectedMusic!!.musicId.toString()
                    )
                    context.startActivity(intent)
                },
                removeAction = {
                    onEvent(MusicEvent.DeleteDialog(isShown = true))
                },
                addToPlaylistAction = { onEvent(MusicEvent.AddToPlaylist) }
            )
        }
    }
}