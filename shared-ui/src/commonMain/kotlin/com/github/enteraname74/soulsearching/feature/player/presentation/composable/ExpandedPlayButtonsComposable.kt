package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.domain.model.PlayerMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedPlayButtonsComposable(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    paddingBottom: Dp = 120.dp,
    mainColor: Color,
    sliderInactiveBarColor: Color,
    onSetFavoriteState: () -> Unit,
    isMusicInFavorite: Boolean,
    currentMusicPosition: Int,
    playerMode: PlayerMode,
    isPlaying: Boolean,
    playbackManager: PlaybackManager = injectElement()
) {
    Column(
        modifier = Modifier
            .composed { modifier }
            .fillMaxWidth(widthFraction)
            .padding(
                start = 28.dp,
                end = 28.dp,
                bottom = paddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val interactionSource = remember { MutableInteractionSource() }
        val sliderColors = SliderDefaults.colors(
            thumbColor = mainColor,
            activeTrackColor = mainColor,
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
                .fillMaxWidth()
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Utils.convertDuration(currentMusicPosition),
                    color = mainColor,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = Utils.convertDuration(playbackManager.currentMusicDuration),
                    color = mainColor,
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    imageVector = when (playerMode) {
                        PlayerMode.NORMAL -> Icons.Rounded.Sync
                        PlayerMode.SHUFFLE -> Icons.Rounded.Shuffle
                        PlayerMode.LOOP -> Icons.Rounded.Replay
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .size(UiConstants.ImageSize.medium)
                        .clickable {
                            playbackManager.changePlayerMode()
                        },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
                Image(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "",
                    modifier = Modifier
                        .size(UiConstants.ImageSize.large)
                        .clickable { playbackManager.previous() },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
                if (isPlaying) {
                    Image(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = "",
                        modifier = Modifier
                            .size(78.dp)
                            .clickable { playbackManager.togglePlayPause() },
                        colorFilter = ColorFilter.tint(color = mainColor)
                    )
                } else {
                    Image(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier
                            .size(78.dp)
                            .clickable { playbackManager.togglePlayPause() },
                        colorFilter = ColorFilter.tint(color = mainColor)
                    )
                }
                Image(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "",
                    modifier = Modifier
                        .size(UiConstants.ImageSize.large)
                        .clickable { playbackManager.next() },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
                Image(
                    imageVector = if (isMusicInFavorite) {
                        Icons.Rounded.Favorite
                    } else {
                        Icons.Rounded.FavoriteBorder
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .size(UiConstants.ImageSize.medium)
                        .clickable {
                            playbackManager.currentMusic?.let {
                                onSetFavoriteState()
                            }
                        },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
            }
        }
    }
}