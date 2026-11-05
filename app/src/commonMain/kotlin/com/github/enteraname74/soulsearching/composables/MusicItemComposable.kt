package com.github.enteraname74.soulsearching.composables

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_drag_handle
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_more_vertical
import com.github.enteraname74.soulsearching.coreui.ext.chainIf
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.feature.multiselection.composable.SoulSelectedIcon
import com.github.enteraname74.soulsearching.feature.multiselection.composable.SoulSelectedIconColors
import com.github.enteraname74.soulsearching.feature.multiselection.composable.SoulSelectedIconDefaults
import com.github.enteraname74.soulsearching.feature.player.domain.state.UserTag
import com.github.enteraname74.soulsearching.feature.player.ext.disabledIfNoAction

@Composable
fun MusicItemComposable(
    modifier: Modifier = Modifier,
    music: Music,
    onClick: ((Music) -> Unit)?,
    onMoreClicked: (() -> Unit)?,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    selectedIconColors: SoulSelectedIconColors = SoulSelectedIconDefaults.secondary(),
    isPlayedMusic: Boolean,
    onLongClick: (() -> Unit)? = null,
    reorderableModifier: Modifier? = null,
    isSelected: Boolean = false,
    isSelectionModeOn: Boolean = false,
    hideMoreIcon: Boolean = false,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.medium),
    leadingSpec: MusicItemLeadingSpec = MusicItemLeadingSpec.Cover,
    userTag: UserTag? = null,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .chainIf(onClick != null) {
                    Modifier.optionalClickable(
                        onClick = {
                            if (isSelectionModeOn) {
                                onLongClick?.invoke()
                            } else {
                                onClick?.invoke(music)
                            }
                        },
                        onLongClick = onLongClick,
                    )
                }
                .padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        end = UiConstants.Spacing.verySmall,
                    ),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val fontWeight = if (isPlayedMusic) FontWeight.Bold else FontWeight.Normal


                FlippableContent(
                    shouldRotate = isSelected,
                    selectedIconColors = selectedIconColors,
                ) {
                    when (leadingSpec) {
                        MusicItemLeadingSpec.Cover -> {
                            SoulImage(
                                cover = music.cover,
                                size = UiConstants.CoverSize.small,
                                tint = textColor,
                            )
                        }
                        is MusicItemLeadingSpec.Position -> {
                            Box(
                                modifier = Modifier
                                    .size(UiConstants.CoverSize.small),
                                contentAlignment = Alignment.Center,
                            ) {
                                SoulText(
                                    text = leadingSpec.pos.toString().toTextSpec(),
                                    color = textColor,
                                    style = UiConstants.Typography.bodyTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = fontWeight
                                )
                            }
                        }
                    }
                }

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

                userTag?.let { UserTagView(it) }

                if (reorderableModifier != null) {
                    SoulIcon(
                        modifier = reorderableModifier,
                        icon = CoreRes.drawable.ic_drag_handle,
                        color = textColor
                    )
                } else if (!hideMoreIcon) {
                    SoulIcon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .optionalClickable(onMoreClicked),
                        icon = CoreRes.drawable.ic_more_vertical,
                        contentDescription = CoreRes.string.more_button.toTextSpec().string,
                        color = textColor.disabledIfNoAction(onMoreClicked),
                    )
                }
            }
        }
    }
}

@Composable
fun UserTagView(
    userTag: UserTag
) {
    Box(
        modifier = Modifier
            .size(UiConstants.ImageSize.smallPlus)
            .background(
                color = userTag.containerColor,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        SoulText(
            text = userTag.username.firstOrNull()?.toString().orEmpty().toTextSpec(),
            style = UiConstants.Typography.bodySmall,
            color = userTag.contentColor
        )
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
        SoulText(
            text = music.name.toTextSpec(),
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
        SoulText(
            text = music.informationText.toTextSpec(),
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
        SoulText(
            text = music.name.toTextSpec(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
        SoulText(
            text = music.album.albumName.toTextSpec(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
        SoulText(
            text = music.artistsNames.toTextSpec(),
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
private fun FlippableContent(
    shouldRotate: Boolean,
    selectedIconColors: SoulSelectedIconColors,
    content: @Composable () -> Unit,
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
            content()
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

sealed interface MusicItemLeadingSpec {
    data object Cover : MusicItemLeadingSpec
    data class Position(val pos: Int) : MusicItemLeadingSpec
}

private const val MAX_ROTATION: Float = 180f
private const val PERSPECTIVE_RATIO: Int = 8
