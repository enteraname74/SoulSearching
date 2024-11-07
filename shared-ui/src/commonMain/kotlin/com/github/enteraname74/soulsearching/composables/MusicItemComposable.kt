package com.github.enteraname74.soulsearching.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.ext.combinedClickableWithRightClick
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIcon
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconColors
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconDefaults
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun MusicItemComposable(
    modifier: Modifier = Modifier,
    music: Music,
    onClick: (Music) -> Unit,
    onMoreClicked: () -> Unit,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    selectedIconColors: SoulSelectedIconColors = SoulSelectedIconDefaults.colors(),
    isPlayedMusic: Boolean,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionModeOn: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickableWithRightClick(
                onClick = {
                    if (isSelectionModeOn) {
                        onLongClick()
                    } else {
                        onClick(music)
                    }
                },
                onLongClick = onLongClick,
            )
            .padding(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlippableImage(
                shouldRotate = isSelected,
                cover = music.cover,
                tint = textColor,
                selectedIconColors = selectedIconColors,
            )
            Column(
                modifier = Modifier
                    .height(UiConstants.CoverSize.small)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music.name,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isPlayedMusic) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isPlayedMusic) FontWeight.Bold else FontWeight.Normal
                )

            }
            SoulIcon(
                modifier = Modifier.clickableWithHandCursor { onMoreClicked() },
                icon = Icons.Rounded.MoreVert,
                contentDescription = strings.moreButton,
                tint = textColor
            )
        }
    }
}

@Composable
private fun FlippableImage(
    shouldRotate: Boolean,
    cover: Cover,
    tint: Color,
    selectedIconColors: SoulSelectedIconColors,
) {
    val rotation by animateFloatAsState(
        targetValue = if (shouldRotate) 180f else 0f,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.normal,
        )
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!shouldRotate) 1f else 0f,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.normal,
        )
    )

    val animateBack by animateFloatAsState(
        targetValue = if (shouldRotate) 1f else 0f,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.normal,
        )
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
    ) {
        if (!shouldRotate) {
            SoulImage(
                cover = cover,
                size = UiConstants.CoverSize.small,
                tint = tint,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animateFront
                        rotationY = rotation
                    }
            )
        } else {
            SoulSelectedIcon(
                colors = selectedIconColors,
                size = UiConstants.CoverSize.small,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animateBack
                        rotationY = rotation
                    }
            )
        }
    }
}