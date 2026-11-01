package com.github.enteraname74.soulsearching.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_favorite_filled
import com.github.enteraname74.soulsearching.coreui.ext.combinedClickableWithRightClick
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.Cover

@Composable
fun BigPreviewComposable(
    modifier: Modifier = Modifier,
    cover: Cover?,
    title: SoulTextSpec?,
    text: SoulTextSpec? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    imageSize: Dp? = UiConstants.ImageSize.veryLarge,
    titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    contentColor: Color = SoulSearchingColorTheme.colorScheme.text,
    selectionColor: Color = SoulSearchingColorTheme.colorScheme.text,
    isFavoritePlaylist: Boolean = false,
    isSelected: Boolean = false,
    isSelectionModeOn: Boolean = false,
    titleMaxLines: Int = 1,
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

    val roundedCorners = RoundedCornerShape(percent = 8)

    Column(
        modifier = modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = roundedCorners,
            )
            .clip(
                shape = RoundedCornerShape(
                    topStartPercent = 8,
                    topEndPercent = 8,
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
            shape = roundedCorners,
            tint = contentColor,
        )
        Row(
            modifier = infoModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
        ) {
            if (isFavoritePlaylist) {
                SoulIcon(
                    icon = CoreRes.drawable.ic_favorite_filled
                )
            }
            Column {
                title?.let {
                    SoulText(
                        text = it,
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        style = titleStyle,
                        maxLines = titleMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                text?.let {
                    SoulText(
                        text = it,
                        color = SoulSearchingColorTheme.colorScheme.textSecondary,
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
