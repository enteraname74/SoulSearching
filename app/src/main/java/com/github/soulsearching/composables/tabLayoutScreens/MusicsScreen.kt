package com.github.soulsearching.composables.tabLayoutScreens

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.Constants
import com.github.soulsearching.ModifyMusicActivity
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.bottomSheets.MusicFileBottomSheet
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicsScreen(
    state: MusicState,
    onEvent: (MusicEvent) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )

    var selectedMusicLongClick : UUID? by rememberSaveable {
        mutableStateOf(null)
    }

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            MusicFileBottomSheet(
                modifyAction = {
                    Log.d("UUID", selectedMusicLongClick!!.toString())
                    val intent = Intent(context, ModifyMusicActivity::class.java)
                    intent.putExtra(
                        "musicId",
                        selectedMusicLongClick!!.toString()
                    )
                    context.startActivity(intent)
                    coroutineScope.launch { modalSheetState.hide() }
                }
            )
        }
    ) {
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
                            selectedMusicLongClick = music.musicId
                            modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                )
            }
        }
    }
}