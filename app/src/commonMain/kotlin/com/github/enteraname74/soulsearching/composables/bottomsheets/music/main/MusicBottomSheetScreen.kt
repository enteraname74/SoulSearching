package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.composables.bottomsheets.MultipleChoiceBottomSheetView
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun MusicBottomSheetScreen(
    viewModel: MusicBottomSheetViewModel,
) {
    val state: MusicBottomSheetState by viewModel.state.collectAsStateWithLifecycle()
    state.dialogState?.Dialog()

    MultipleChoiceBottomSheetView(
        topInformation = state.bottomSheetTopInformation,
        choices = state.rowSpecs,
    )
}