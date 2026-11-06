package com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulCard
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SettingsStatisticsSmallCard(
    spec: SettingsStatisticsSmallCardSpec,
    modifier: Modifier = Modifier,
) {
    SoulCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    all = UiConstants.Spacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulText(
                text = spec.title,
                style = UiConstants.Typography.bodyTitle,
                color = SoulSearchingColorTheme.colorScheme.textSecondary,
            )
            SoulText(
                text = spec.text,
                style = UiConstants.Typography.titleSmall,
                color = SoulSearchingColorTheme.colorScheme.text,
            )
            SoulText(
                text = spec.description,
                style = UiConstants.Typography.body,
                color = SoulSearchingColorTheme.colorScheme.textTertiary,
            )
        }
    }
}

data class SettingsStatisticsSmallCardSpec(
    val title: SoulTextSpec,
    val text: SoulTextSpec,
    val description: SoulTextSpec,
)