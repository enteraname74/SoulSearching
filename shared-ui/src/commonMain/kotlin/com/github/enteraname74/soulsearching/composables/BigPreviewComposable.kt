package com.github.soulsearching.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigPreviewComposable(
    modifier : Modifier = Modifier,
    image : ImageBitmap?,
    title : String,
    text : String = "",
    onClick : () -> Unit,
    onLongClick : () -> Unit = {},
    imageSize : Dp = UiConstants.ImageSize.veryLarge,
    titleStyle : TextStyle = MaterialTheme.typography.labelLarge,
    textStyle : TextStyle = MaterialTheme.typography.labelSmall,
    roundedPercent : Int = 4,
    isFavoritePlaylist: Boolean = false
) {
    Column(
        modifier = Modifier
            .width(imageSize)
            .composed {
                modifier
            }
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    onLongClick()
                }
            )

    ) {
        SoulImage(bitmap = image, size = imageSize, roundedPercent = roundedPercent)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            if (isFavoritePlaylist) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "",
                    tint = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = SoulSearchingColorTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    style = titleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        color = SoulSearchingColorTheme.colorScheme.onSecondary,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}