package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.combinedClickableWithRightClick
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun BigPreviewComposable(
    modifier: Modifier = Modifier,
    cover: Cover?,
    title: String,
    text: String = "",
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    imageSize: Dp = UiConstants.ImageSize.veryLarge,
    titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    roundedPercent: Int = 4,
    isFavoritePlaylist: Boolean = false,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .combinedClickableWithRightClick(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        SoulImage(
            cover = cover,
            size = imageSize,
            roundedPercent = roundedPercent
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            if (isFavoritePlaylist) {
                SoulIcon(
                    icon = Icons.Rounded.Favorite,
                    contentDescription = null,
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = titleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}