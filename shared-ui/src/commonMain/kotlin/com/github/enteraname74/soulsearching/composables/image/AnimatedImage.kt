package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun <T>AnimatedImage(
    data: T?,
    tint: Color,
    contentScale: ContentScale,
    modifier: Modifier,
    onData: @Composable (data: T) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = data != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            if (data != null) {
                onData(data)
            }
        }

        AnimatedVisibility(
            visible = data == null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            TemplateImage(
                modifier = modifier,
                contentScale = contentScale,
                tint = tint,
            )
        }
    }
}