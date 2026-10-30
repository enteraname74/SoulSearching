package com.github.enteraname74.soulsearching.composables.bottomsheets

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun BottomSheetElementInformation(
    cover: Cover?,
    title: SoulTextSpec,
    subTitle: SoulTextSpec?,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.large,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SoulImage(
                    cover = cover,
                    size = UiConstants.CoverSize.small,
                    tint = SoulSearchingColorTheme.colorScheme.text,
                )
                Column(
                    modifier = Modifier
                        .height(UiConstants.CoverSize.small)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    SoulText(
                        text = title,
                        color = SoulSearchingColorTheme.colorScheme.text,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    subTitle?.let {
                        SoulText(
                            text = it,
                            color = SoulSearchingColorTheme.colorScheme.textSecondary,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = SoulSearchingColorTheme.colorScheme.textSecondary,
        )
    }
}
