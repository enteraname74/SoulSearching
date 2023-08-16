package com.github.soulsearching.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    )
}
