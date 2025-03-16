package com.github.enteraname74.soulsearching.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.ext.combinedClickableWithRightClick
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIcon
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconColors
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconDefaults
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize

@Composable
fun MusicItemComposable(
    modifier: Modifier = Modifier,
    music: Music,
    onClick: (Music) -> Unit,
    onMoreClicked: () -> Unit,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    selectedIconColors: SoulSelectedIconColors = SoulSelectedIconDefaults.secondary(),
    isPlayedMusic: Boolean,
    onLongClick: () -> Unit = {},
    reorderableModifier: Modifier? = null,
    isSelected: Boolean = false,
    isSelectionModeOn: Boolean = false,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.medium)
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
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
                .padding(padding),
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

                val fontWeight = if (isPlayedMusic) FontWeight.Bold else FontWeight.Normal

                when {
                    this@BoxWithConstraints.maxWidth > WindowSize.Medium.maxValue -> {
                        MusicInformationWideView(
                            music = music,
                            textColor = textColor,
                            fontWeight = fontWeight,
                        )
                    }
                    else -> {
                        MusicInformationDefaultView(
                            music = music,
                            textColor = textColor,
                            fontWeight = fontWeight,
                        )
                    }
                }

                if (reorderableModifier != null) {
                    SoulIcon(
                        modifier = reorderableModifier,
                        icon = Icons.Rounded.DragHandle,
                        tint = textColor
                    )
                } else {
                    SoulIcon(
                        modifier = Modifier.clickableWithHandCursor { onMoreClicked() },
                        icon = Icons.Rounded.MoreVert,
                        contentDescription = strings.moreButton,
                        tint = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.MusicInformationDefaultView(
    music: Music,
    textColor: Color,
    fontWeight: FontWeight,
) {
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
            fontWeight = fontWeight
        )
        Text(
            text = music.informationText,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun RowScope.MusicInformationWideView(
    music: Music,
    textColor: Color,
    fontWeight: FontWeight,
) {
    Row(
        modifier = Modifier
            .height(UiConstants.CoverSize.small)
            .weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            text = music.name,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
        Text(
            text = music.album,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
        Text(
            text = music.artist,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
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
        targetValue = if (shouldRotate) MAX_ROTATION else 0f,
        animationSpec = tween(
            durationMillis = UiConstants.AnimationDuration.normal,
        )
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = PERSPECTIVE_RATIO * density
            }
    ) {
        if (rotation <= MAX_ROTATION / 2) {
            SoulImage(
                cover = cover,
                size = UiConstants.CoverSize.small,
                tint = tint,
            )
        } else {
            SoulSelectedIcon(
                colors = selectedIconColors,
                size = UiConstants.CoverSize.small,
                modifier = Modifier
                    .graphicsLayer {
                        rotationY = MAX_ROTATION
                    }
            )
        }
    }
}

private const val MAX_ROTATION: Float = 180f
private const val PERSPECTIVE_RATIO: Int = 8