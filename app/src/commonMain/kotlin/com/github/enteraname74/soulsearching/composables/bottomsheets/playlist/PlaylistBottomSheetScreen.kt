package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.composables.bottomsheets.MultipleChoiceBottomSheetView

@Composable
fun PlaylistBottomSheetScreen(
    viewModel: PlaylistBottomSheetViewModel,
) {
    val state: PlaylistBottomSheetState by viewModel.state.collectAsStateWithLifecycle()
    state.dialogState?.Dialog()

    MultipleChoiceBottomSheetView(
        topInformation = state.bottomSheetTopInformation,
        choices = state.rowSpecs,
    )
}