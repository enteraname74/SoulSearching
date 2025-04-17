package com.github.enteraname74.soulsearching.coreui.composable

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulCircularProgressIndicator() {
    CircularProgressIndicator(
        color = SoulSearchingColorTheme.colorScheme.onPrimary,
    )
}