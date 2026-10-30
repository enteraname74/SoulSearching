package com.github.enteraname74.soulsearching.composables

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun ProgressIndicatorComposable(
    progressMessage: SoulTextSpec,
    progress: Float,
    subText: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SoulText(
            modifier = Modifier.padding(UiConstants.Spacing.medium),
            text = progressMessage,
            color = SoulSearchingColorTheme.colorScheme.text,
            textAlign = TextAlign.Center
        )
        LinearProgressIndicator(
            progress = { progress },
            color = SoulSearchingColorTheme.colorScheme.text,
            trackColor = SoulSearchingColorTheme.colorScheme.surface,
            drawStopIndicator = {},
        )
        subText?.let {
            SoulText(
                modifier = Modifier.padding(UiConstants.Spacing.medium),
                text = SoulTexts.fetchingMusicsFrom(path = it),
                color = SoulSearchingColorTheme.colorScheme.text,
                textAlign = TextAlign.Center
            )
        }
    }

}
