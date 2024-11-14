package com.github.enteraname74.soulsearching.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    imageSize: Dp? = UiConstants.ImageSize.veryLarge,
    titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    selectionColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    roundedPercent: Int = 4,
    isFavoritePlaylist: Boolean = false,
    isSelected: Boolean = false,
    isSelectionModeOn: Boolean = false,
) {
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) BORDER_SIZE else 0.dp,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.normal,
        ),
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) selectionColor else Color.Transparent,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.normal,
        ),
    )

    Column(
        modifier = modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(
                    roundedPercent
                )
            )
            .combinedClickableWithRightClick(
                onClick = {
                    if (isSelectionModeOn) {
                        onLongClick()
                    } else {
                        onClick()
                    }
                },
                onLongClick = onLongClick
            )
    ) {

        val imageModifier = if (imageSize == null) {
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        } else {
            Modifier
        }

        val infoModifier = if (imageSize == null) {
            Modifier
        } else {
            Modifier.width(imageSize)
        }

        SoulImage(
            modifier = imageModifier,
            cover = cover,
            size = imageSize,
            roundedPercent = roundedPercent,
        )
        Row(
            modifier = infoModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            if (isFavoritePlaylist) {
                SoulIcon(icon = Icons.Rounded.Favorite)
            }
            Column {
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

private val BORDER_SIZE: Dp = 4.dp