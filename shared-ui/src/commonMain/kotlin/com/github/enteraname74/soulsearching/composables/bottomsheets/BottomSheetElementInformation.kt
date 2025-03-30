package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun BottomSheetElementInformation(
    cover: Cover?,
    title: String,
    subTitle: String?,
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
                    tint = SoulSearchingColorTheme.colorScheme.onSecondary,
                )
                Column(
                    modifier = Modifier
                        .height(UiConstants.CoverSize.small)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = SoulSearchingColorTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    subTitle?.let {
                        Text(
                            text = it,
                            color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
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
            color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
        )
    }
}