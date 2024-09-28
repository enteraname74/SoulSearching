package com.github.enteraname74.soulsearching.coreui.bottomsheet

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulBottomSheetHandler(
    onClose: () -> Unit,
    colors: SoulBottomSheetColors = SoulBottomSheetDefaults.colors(),
    content: @Composable (closeWithAnim: () -> Unit) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val closeWithAnim: () -> Unit = {
        coroutineScope.launch {
            bottomSheetState.hide()
        }.invokeOnCompletion {
            onClose()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = bottomSheetState,
        containerColor = colors.containerColor,
        contentColor = colors.contentColor,
        dragHandle = null,
        windowInsets = WindowInsets.displayCutout
    ) {
        content(closeWithAnim)
    }
}

object SoulBottomSheetDefaults {
    @Composable
    fun colors(): SoulBottomSheetColors = SoulBottomSheetColors(
        containerColor = SoulSearchingColorTheme.colorScheme.secondary,
        contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
    )
}

data class SoulBottomSheetColors(
    val containerColor: Color,
    val contentColor: Color,
)