package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.ext.imageVector


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedPlayerControlsComposable(
    modifier: Modifier = Modifier,
    contentColor: Color,
    sliderInactiveBarColor: Color,
    onSetFavoriteState: () -> Unit,
    isMusicInFavorite: Boolean,
    currentMusicPosition: Int,
    playerMode: PlayerMode,
    isPlaying: Boolean,
    playbackManager: PlaybackManager = injectElement()
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val interactionSource = remember { MutableInteractionSource() }
        val sliderColors = SliderDefaults.colors(
            thumbColor = contentColor,
            activeTrackColor = contentColor,
            inactiveTrackColor = sliderInactiveBarColor
        )

        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = currentMusicPosition.toFloat(),
            onValueChange = {
                playbackManager.seekToPosition(it.toInt())
            },
            colors = sliderColors,
            valueRange = 0f..playbackManager.currentMusicDuration.toFloat(),
            interactionSource = interactionSource,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .size(UiConstants.ImageSize.small)
                        .padding(
                            start = 4.dp,
                            top = 4.dp
                        ),
                    colors = sliderColors
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(
                    weight = 1f,
                    fill = false,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {

            SliderMusidPositionAndDuration(
                contentColor = contentColor,
                currentMusicPosition = currentMusicPosition,
            )

            PlayerControls(
                playerMode = playerMode,
                isPlaying = isPlaying,
                isMusicInFavorite = isMusicInFavorite,
                onSetFavoriteState = onSetFavoriteState,
                contentColor = contentColor,
            )
        }
    }
}

@Composable
private fun SliderMusidPositionAndDuration(
    contentColor: Color,
    currentMusicPosition: Int,
    playbackManager: PlaybackManager = injectElement(),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = Utils.convertDuration(currentMusicPosition),
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = Utils.convertDuration(playbackManager.currentMusicDuration),
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun PlayerControls(
    playbackManager: PlaybackManager = injectElement(),
    playerMode: PlayerMode,
    isMusicInFavorite: Boolean,
    isPlaying: Boolean,
    onSetFavoriteState: () -> Unit,
    contentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = playerMode.imageVector(),
            contentDescription = null,
            modifier = Modifier
                .weight(ExternalIconsWeight)
                .height(UiConstants.ImageSize.medium)
                .widthIn(max = UiConstants.ImageSize.medium)
                .clickable {
                    playbackManager.changePlayerMode()
                },
            colorFilter = ColorFilter.tint(color = contentColor)
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        Image(
            imageVector = Icons.Rounded.SkipPrevious,
            contentDescription = null,
            modifier = Modifier
                .weight(InternalIconsWeight)
                .height(UiConstants.ImageSize.large)
                .widthIn(max = UiConstants.ImageSize.large)
                .clickable { playbackManager.previous() },
            colorFilter = ColorFilter.tint(color = contentColor)
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        Image(
            imageVector = if (isPlaying) {
                Icons.Rounded.Pause
            } else {
                Icons.Rounded.PlayArrow
            },
            contentDescription = null,
            modifier = Modifier
                .weight(MainIconWeight)
                .height(UiConstants.Player.playerPlayerButtonSize)
                .widthIn(max = UiConstants.Player.playerPlayerButtonSize)
                .clickable { playbackManager.togglePlayPause() },
            colorFilter = ColorFilter.tint(color = contentColor)
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        Image(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = null,
            modifier = Modifier
                .weight(InternalIconsWeight)
                .height(UiConstants.ImageSize.large)
                .widthIn(max = UiConstants.ImageSize.large)
                .clickable { playbackManager.next() },
            colorFilter = ColorFilter.tint(color = contentColor)
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        Image(
            imageVector = if (isMusicInFavorite) {
                Icons.Rounded.Favorite
            } else {
                Icons.Rounded.FavoriteBorder
            },
            contentDescription = null,
            modifier = Modifier
                .weight(ExternalIconsWeight)
                .height(UiConstants.ImageSize.medium)
                .widthIn(max = UiConstants.ImageSize.medium)
                .clickable {
                    playbackManager.currentMusic?.let {
                        onSetFavoriteState()
                    }
                },
            colorFilter = ColorFilter.tint(color = contentColor)
        )
    }
}

private const val SpacerWeight: Float = 0.2f
private const val ExternalIconsWeight: Float = 0.4f
private const val InternalIconsWeight: Float = 0.7f
private const val MainIconWeight: Float = 1f
