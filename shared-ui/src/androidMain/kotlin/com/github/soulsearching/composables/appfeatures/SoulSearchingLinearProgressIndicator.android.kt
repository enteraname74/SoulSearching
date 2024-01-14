package com.github.soulsearching.composables.appfeatures

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import com.github.soulsearching.theme.SoulSearchingColorTheme

@Composable
actual fun SoulSearchingLinearProgressIndicator(
    progress: Float
) {
    LinearProgressIndicator(
        progress = { progress },
        color = SoulSearchingColorTheme.colorScheme.onSecondary,
        trackColor = SoulSearchingColorTheme.colorScheme.secondary,
    )
}