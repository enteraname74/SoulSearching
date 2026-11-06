package com.github.enteraname74.soulsearching.feature.settings.statistics.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.Cover

@Composable
fun SettingsStatisticsProgressRow(
    position: Int,
    title: SoulTextSpec,
    cover: Cover?,
    progress: Float,
    total: SoulTextSpec,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.medium),
    onClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .optionalClickable(
                onClick = onClick,
            )
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large),
    ) {
        SoulText(
            text = SoulTextSpec.Raw("$position"),
            color = SoulSearchingColorTheme.colorScheme.textTertiary,
            style = UiConstants.Typography.body,
        )
        SoulImage(
            cover = cover,
            size = UiConstants.CoverSize.small
        )
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulText(
                text = title,
                color = SoulSearchingColorTheme.colorScheme.text,
                style = UiConstants.Typography.body.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            ProgressBar(progress)
        }
        SoulText(
            modifier = Modifier
                .padding(
                    start = UiConstants.Spacing.medium,
                ),
            text = total,
            color = SoulSearchingColorTheme.colorScheme.text,
            style = UiConstants.Typography.body,
        )
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(
                color = SoulSearchingColorTheme.colorScheme.surfaceHigh,
                shape = CircleShape,
            )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth(progress)
            .height(2.dp)
            .background(
                color = SoulSearchingColorTheme.colorScheme.accent,
                shape = CircleShape,
            )
    )
}