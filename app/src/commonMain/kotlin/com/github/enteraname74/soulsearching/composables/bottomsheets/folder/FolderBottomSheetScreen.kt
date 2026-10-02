package com.github.enteraname74.soulsearching.composables.bottomsheets.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.composables.bottomsheets.MultipleChoiceBottomSheetView

@Composable
internal fun FolderBottomSheetScreen(
    viewModel: FolderBottomSheetViewModel,
) {
    val state: FolderBottomSheetState by viewModel.state.collectAsStateWithLifecycle()
    state.dialogState?.Dialog()

    MultipleChoiceBottomSheetView(
        topInformation = state.bottomSheetTopInformation,
        choices = state.rowSpecs,
    )
}
