package com.github.enteraname74.soulsearching.coreui.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulBottomSheetHandler(
    onClose: () -> Unit,
    content: @Composable (closeWithAnim: () -> Unit) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val closeWithAnim = {
        coroutineScope.launch {
            bottomSheetState.hide()
        }.invokeOnCompletion {
            onClose()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = bottomSheetState,
    ) {
        content(closeWithAnim)
    }
}