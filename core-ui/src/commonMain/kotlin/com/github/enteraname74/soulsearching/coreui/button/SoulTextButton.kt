package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.github.enteraname74.soulsearching.coreui.UiConstants

@Composable
fun SoulTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = SoulButtonDefaults.ROUND_SHAPE,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
    contentPadding: PaddingValues = SoulButtonDefaults.contentPadding(),
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .pointerHoverIcon(PointerIcon.Hand),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colors.contentColor,
        ),
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            color = colors.contentColor,
            style = UiConstants.Typography.body,
        )
    }
}
