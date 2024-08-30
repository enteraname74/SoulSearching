package com.github.enteraname74.soulsearching.feature.search.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LinearPreviewComposable(
    title: String,
    text: String,
    coverId: UUID?,
    onLongClick: (() -> Unit)?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.medium)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .optionalClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoulImage(
                coverId = coverId,
                size = UiConstants.CoverSize.small
            )
            Column(
                modifier = Modifier
                    .height(UiConstants.CoverSize.small)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = text,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }
            onLongClick?.let {
                Icon(
                    modifier = Modifier.optionalClickable(onClick = it),
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = strings.moreButton,
                    tint = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
        }
    }
}