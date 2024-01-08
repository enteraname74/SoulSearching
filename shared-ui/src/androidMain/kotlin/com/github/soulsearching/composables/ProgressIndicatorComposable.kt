package com.github.soulsearching.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.DynamicColor

@Composable
fun ProgressIndicatorComposable(
    progressMessage: String,
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(Constants.Spacing.medium),
            text = progressMessage,
            color = DynamicColor.onPrimary,
            textAlign = TextAlign.Center
        )
        LinearProgressIndicator(
            progress = { progress },
            color = DynamicColor.onSecondary,
            trackColor = DynamicColor.secondary,
        )
    }

}