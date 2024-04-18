package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.mainpage.presentation.composable.vertical
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    playlistsWithMusics: List<PlaylistWithMusics>,
    onDismiss: () -> Unit,
    onConfirm: (selectedPlaylistsIds: List<UUID>) -> Unit,
    addToPlaylistModalSheetState: SheetState,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        windowInsets = WindowInsets.statusBars,
        onDismissRequest = onDismiss,
        sheetState = addToPlaylistModalSheetState,
        dragHandle = {}
    ) {
        AddToPlaylistMenuBottomSheet(
            primaryColor = primaryColor,
            textColor = textColor,
            onDismiss = {
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!addToPlaylistModalSheetState.isVisible) onDismiss()
                    }
            },
            onConfirm = { selectedPlaylistsIds ->
                coroutineScope.launch { addToPlaylistModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!addToPlaylistModalSheetState.isVisible) {
                            onConfirm(selectedPlaylistsIds)
                        }
                    }
            },
            playlistsWithMusics = playlistsWithMusics
        )
    }
}