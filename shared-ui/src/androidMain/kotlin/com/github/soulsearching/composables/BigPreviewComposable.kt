package com.github.soulsearching.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigPreviewComposable(
    modifier : Modifier = Modifier,
    image : ImageBitmap?,
    title : String,
    text : String,
    onClick : () -> Unit,
    onLongClick : () -> Unit = {},
    imageSize : Dp = Constants.ImageSize.veryLarge,
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
        AppImage(bitmap = image, size = imageSize, roundedPercent = roundedPercent)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.small)
        ) {
            if (isFavoritePlaylist) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "",
                    tint = DynamicColor.onPrimary
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = DynamicColor.onSecondary,
                    fontWeight = FontWeight.Bold,
                    style = titleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = text,
                    color = DynamicColor.onSecondary,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}