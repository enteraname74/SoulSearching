package com.github.enteraname74.soulsearching.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
expect fun CoverPathImage(
    initialCoverPath: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
)