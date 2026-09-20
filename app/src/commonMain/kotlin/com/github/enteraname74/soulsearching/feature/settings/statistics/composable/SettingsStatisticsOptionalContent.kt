package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics

@Composable
internal fun SettingsStatisticsOptionalContent(
    optionalContent: PeriodUiStatistics.Stats.OptionalContent,
    modifier: Modifier = Modifier,
) {
    val text by optionalContent.flow.collectAsStateWithLifecycle("")
    Column(
        modifier = modifier,
    ) {
        Text(
            text = optionalContent.title,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.bodyTitle,
        )
        Text(
            text = text,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.body,
        )
    }
}