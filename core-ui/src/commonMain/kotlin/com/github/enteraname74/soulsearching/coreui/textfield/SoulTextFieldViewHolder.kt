package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants

@Composable
internal fun SoulTextFieldViewHolder(
    colors: SoulTextFieldColors,
    style: SoulTextFieldStyle,
    isFocused: Boolean,
    textField: @Composable () -> Unit,
) {
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) SoulTextFieldDefaults.FOCUSED_BORDER_SIZE else 0.dp,
        animationSpec = tween(UiConstants.AnimationDuration.normal),
    )

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) colors.contentColor else Color.Transparent,
        animationSpec = tween(UiConstants.AnimationDuration.normal),
    )

    Box(
        modifier = Modifier
            .animateContentSize()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = style.shape(),
            )
            .background(
                color = colors.containerColor,
                shape = style.shape(),
            )
    ) {
        textField()
    }
}