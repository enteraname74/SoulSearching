package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigPreviewComposable(
    modifier : Modifier = Modifier,
    coverId: UUID?,
    title : String,
    text : String = "",
    onClick : () -> Unit,
    onLongClick : () -> Unit = {},
    imageSize : Dp = UiConstants.ImageSize.veryLarge,
    titleStyle : TextStyle = MaterialTheme.typography.labelLarge,
    textStyle : TextStyle = MaterialTheme.typography.labelSmall,
    roundedPercent : Int = 4,
    isFavoritePlaylist: Boolean = false,
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
        SoulImage(
            coverId = coverId,
            size = imageSize,
            roundedPercent = roundedPercent
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth()
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