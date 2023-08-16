package com.github.soulsearching.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerSpacer() {
    Spacer(
        modifier = Modifier
            .height(80.dp)
    )
}
