package com.github.soulsearching.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun ProgressIndicatorComposable(
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(Constants.Spacing.medium),
            text = stringResource(id = R.string.searching_songs_from_your_device),
            color = DynamicColor.onPrimary,
            textAlign = TextAlign.Center
        )
        LinearProgressIndicator(
            progress = progress,
            trackColor = DynamicColor.secondary,
            color = DynamicColor.onSecondary
        )
    }

}