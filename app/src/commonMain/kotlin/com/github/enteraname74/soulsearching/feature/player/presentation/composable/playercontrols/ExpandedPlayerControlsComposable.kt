package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.ext.imageVector


@Composable
fun ExpandedPlayerControlsComposable(
    modifier: Modifier = Modifier,
    currentMusicProgression: Int,
    toggleFavoriteState: () -> Unit,
    state: PlayerViewState.Data,
    seekTo: (newPosition: Int) -> Unit,
    changePlayerMode: () -> Unit,
    previous: () -> Unit,
    togglePlayPause: () -> Unit,
    next: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var draggedThumbValue: Float? by rememberSaveable { mutableStateOf(null) }

        SoulSlider(
            maxValue = state.currentMusic.duration.toFloat(),
            value = currentMusicProgression.toFloat(),
            onValueChanged = { seekTo(it.toInt()) },
            onThumbDragged = { draggedThumbValue = it }
        )

        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {

            SliderMusicPositionAndDuration(
                contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                currentMusicPosition = draggedThumbValue?.toInt() ?: currentMusicProgression,
                currentMusicDuration = state.currentMusic.duration.toInt(),
            )

            PlayerControls(
                playerMode = state.playerMode,
                isPlaying = state.isPlaying,
                isMusicInFavorite = state.isCurrentMusicInFavorite,
                toggleFavoriteState = toggleFavoriteState,
                contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                previous = previous,
                next = next,
                togglePlayPause = togglePlayPause,
                changePlayerMode = changePlayerMode,
            )
        }
    }
}

@Composable
private fun SliderMusicPositionAndDuration(
    contentColor: Color,
    currentMusicPosition: Int,
    currentMusicDuration: Int,
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
            text = Utils.convertDuration(currentMusicDuration),
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun PlayerControls(
    playerMode: PlayerMode,
    isMusicInFavorite: Boolean,
    isPlaying: Boolean,
    changePlayerMode: () -> Unit,
    previous: () -> Unit,
    togglePlayPause: () -> Unit,
    toggleFavoriteState: () -> Unit,
    next: () -> Unit,
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
                .clickableWithHandCursor {
                    changePlayerMode()
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
                .clickableWithHandCursor { previous() },
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
                .clickableWithHandCursor { togglePlayPause() },
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
                .clickableWithHandCursor { next() },
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
                .clickableWithHandCursor { toggleFavoriteState() },
            colorFilter = ColorFilter.tint(color = contentColor)
        )
    }
}

private const val SpacerWeight: Float = 0.2f
private const val ExternalIconsWeight: Float = 0.4f
private const val InternalIconsWeight: Float = 0.7f
private const val MainIconWeight: Float = 1f
