package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.statistics.model.PeriodUiStatistics
import com.github.enteraname74.soulsearching.coreui.strings.SoulText

@Composable
internal fun SettingsStatisticsOptionalContent(
    optionalContent: PeriodUiStatistics.Stats.OptionalContent,
    modifier: Modifier = Modifier,
) {
    val text by optionalContent.flow.collectAsStateWithLifecycle("".toTextSpec())
    Column(
        modifier = modifier,
    ) {
        SoulText(
            text = optionalContent.title,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.bodyTitle,
        )
        SoulText(
            text = text,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.body,
        )
    }
}