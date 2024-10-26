package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun ProgressIndicatorComposable(
    progressMessage: String,
    progress: Float,
    subText: String?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(UiConstants.Spacing.medium),
            text = progressMessage,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
        LinearProgressIndicator(
            progress = { progress },
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            trackColor = SoulSearchingColorTheme.colorScheme.secondary,
            drawStopIndicator = {},
        )
        subText?.let {
            Text(
                modifier = Modifier.padding(UiConstants.Spacing.medium),
                text = strings.fetchingMusicsFrom(path = it),
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }

}