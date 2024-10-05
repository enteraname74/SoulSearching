package com.github.enteraname74.soulsearching.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.toUri

@Composable
actual fun CoverPathImage(
    initialCoverPath: String?,
    modifier: Modifier,
    tint: Color,
    contentScale: ContentScale,
) {
    if (initialCoverPath != null) {
        DataImage(
            data = initialCoverPath.toUri(),
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        TemplateImage(
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
        )
    }
}