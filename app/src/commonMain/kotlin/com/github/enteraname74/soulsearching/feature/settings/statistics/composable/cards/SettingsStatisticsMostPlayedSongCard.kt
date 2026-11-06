package com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulCard
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.domain.model.Music

@Composable
fun SettingsStatisticsMusicStatisticsCard(
    spec: SettingsStatisticsMostPlayedSongSpec,
    modifier: Modifier = Modifier,
) {
    when (rememberWindowSize()) {
        WindowSize.Small -> SmallView(
            spec = spec,
            modifier = modifier,
        )
        WindowSize.Medium -> MediumView(
            spec = spec,
            modifier = modifier,
        )
        WindowSize.Large -> LargeView(
            spec = spec,
            modifier = modifier,
        )
    }
}

@Composable
private fun LargeView(
    spec: SettingsStatisticsMostPlayedSongSpec,
    modifier: Modifier = Modifier,
) {
    SoulCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = SoulSearchingColorTheme.colorScheme.surface.blend(
                SoulSearchingColorTheme.colorScheme.accent,
                .14f,
            )
        )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    all = UiConstants.Spacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        ) {
            SoulText(
                text = spec.title,
                color = SoulSearchingColorTheme.colorScheme.textSecondary,
                style = UiConstants.Typography.bodyTitle,
            )
            SoulImage(
                cover = spec.music.cover,
                size = UiConstants.ImageSize.veryHuge,
                shape = RoundedCornerShape(UiConstants.Spacing.large),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
                ) {
                    SoulText(
                        text = SoulTextSpec.Raw(spec.music.name),
                        color = SoulSearchingColorTheme.colorScheme.text,
                        style = UiConstants.Typography.titleSmall,
                        maxLines = 1,
                    )
                    SoulText(
                        text = SoulTextSpec.Raw(spec.music.artistsNames),
                        color = SoulSearchingColorTheme.colorScheme.textSecondary,
                        style = UiConstants.Typography.body,
                        maxLines = 1,
                    )
                }
                SoulText(
                    text = spec.value,
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.bodyTitle,
                )
            }
        }
    }
}

@Composable
private fun MediumView(
    spec: SettingsStatisticsMostPlayedSongSpec,
    modifier: Modifier = Modifier,
) {
    SoulCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = SoulSearchingColorTheme.colorScheme.surface.blend(
                SoulSearchingColorTheme.colorScheme.accent,
                .14f,
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    all = UiConstants.Spacing.large,
                ),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
        ) {
            SoulImage(
                cover = spec.music.cover,
                size = UiConstants.ImageSize.veryLarge
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.veryLarge),
            ) {
                SoulText(
                    text = spec.title,
                    color = SoulSearchingColorTheme.colorScheme.textSecondary,
                    style = UiConstants.Typography.bodyTitle,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
                ) {
                    SoulText(
                        text = SoulTextSpec.Raw(spec.music.name),
                        color = SoulSearchingColorTheme.colorScheme.text,
                        style = UiConstants.Typography.titleSmall,
                        maxLines = 1,
                    )
                    SoulText(
                        text = SoulTextSpec.Raw(spec.music.artistsNames),
                        color = SoulSearchingColorTheme.colorScheme.textSecondary,
                        style = UiConstants.Typography.body,
                        maxLines = 1,
                    )
                }
                SoulText(
                    text = spec.value,
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.bodyTitle,
                )
            }
        }
    }
}

@Composable
private fun SmallView(
    spec: SettingsStatisticsMostPlayedSongSpec,
    modifier: Modifier = Modifier,
) {
    SoulCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = SoulSearchingColorTheme.colorScheme.surface.blend(
                SoulSearchingColorTheme.colorScheme.accent,
                .14f,
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    all = UiConstants.Spacing.mediumPlus,
                ),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoulImage(
                cover = spec.music.cover,
                size = UiConstants.CoverSize.medium,
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            ) {
                SoulText(
                    text = spec.title,
                    color = SoulSearchingColorTheme.colorScheme.textSecondary,
                    style = UiConstants.Typography.bodySmall,
                )
                SoulText(
                    text = SoulTextSpec.Raw(spec.music.name),
                    color = SoulSearchingColorTheme.colorScheme.text,
                    style = UiConstants.Typography.bodyTitle,
                    maxLines = 1,
                )
                SoulText(
                    text = SoulTextSpec.Raw(spec.music.artistsNames),
                    color = SoulSearchingColorTheme.colorScheme.textSecondary,
                    style = UiConstants.Typography.body,
                    maxLines = 1,
                )
            }
            SoulText(
                text = spec.value,
                color = SoulSearchingColorTheme.colorScheme.text,
                style = UiConstants.Typography.bodyTitle,
            )
        }
    }
}

data class SettingsStatisticsMostPlayedSongSpec(
    val title: SoulTextSpec,
    val music: Music,
    val value: SoulTextSpec,
)