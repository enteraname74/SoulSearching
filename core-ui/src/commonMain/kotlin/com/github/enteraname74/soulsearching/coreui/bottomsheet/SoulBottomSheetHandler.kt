package com.github.enteraname74.soulsearching.coreui.bottomsheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
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

    BottomSheetDefaults.ExpandedShape

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = bottomSheetState,
        shape = BOTTOM_SHEET_SHAPE,
        containerColor = colors.containerColor,
        contentColor = colors.contentColor,
        dragHandle = null,
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

private val BOTTOM_SHEET_SHAPE: Shape = RoundedCornerShape(
    topStart = 14.0.dp,
    topEnd = 14.0.dp,
    bottomEnd = 0.0.dp,
    bottomStart = 0.0.dp
)