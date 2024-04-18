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
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme

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
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
        LinearProgressIndicator(
            progress = progress,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            trackColor = SoulSearchingColorTheme.colorScheme.secondary,
        )
    }

}