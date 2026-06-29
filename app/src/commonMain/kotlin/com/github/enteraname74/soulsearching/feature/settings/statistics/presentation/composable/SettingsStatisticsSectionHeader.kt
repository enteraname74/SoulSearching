package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.ListenedElement
import com.github.enteraname74.soulsearching.util.CoverUtils

@Composable
fun SettingsStatisticsSectionHeader(
    element: ListenedElement,
    modifier: Modifier = Modifier,
) {

    val windowSize = rememberWindowSize()

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(all = UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulImage(
                cover = element.cover,
                size = if (windowSize == WindowSize.Small) {
                    UiConstants.ImageSize.huge
                } else {
                    UiConstants.ImageSize.veryHuge
                },
                roundedPercent = 5,
                contentScale = ContentScale.Fit,
                builderOptions = {
                    this.size(CoverUtils.IMAGE_SIZE)
                }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            ) {
                Text(
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    text = element.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    text = element.text(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}