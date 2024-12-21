package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SoulTextFieldStyle {
    Top,
    Bottom,
    Body,
    Unique
    ;
}

internal fun SoulTextFieldStyle.shape(): Shape =
    when (this) {
        SoulTextFieldStyle.Top -> RoundedCornerShape(
            topStart = CORNER_SIZE,
            topEnd = CORNER_SIZE,
        )
        SoulTextFieldStyle.Bottom -> RoundedCornerShape(
            bottomStart = CORNER_SIZE,
            bottomEnd = CORNER_SIZE,
        )
        SoulTextFieldStyle.Body -> RectangleShape
        SoulTextFieldStyle.Unique -> RoundedCornerShape(CORNER_SIZE)
    }

private val CORNER_SIZE: Dp = 10.dp