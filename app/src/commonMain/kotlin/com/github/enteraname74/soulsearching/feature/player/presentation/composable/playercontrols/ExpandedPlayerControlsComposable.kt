package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.model.PlayerMode
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_favorite
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_favorite_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_pause_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_skip_next_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_skip_previous_filled
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.utils.Utils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.ext.icon


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
        SoulIcon(
            icon = playerMode.icon(),
            modifier = Modifier
                .weight(ExternalIconsWeight)
                .clip(CircleShape)
                .height(UiConstants.ImageSize.medium)
                .widthIn(max = UiConstants.ImageSize.medium)
                .clickableWithHandCursor {
                    changePlayerMode()
                },
            color = contentColor,
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_previous_filled,
            modifier = Modifier
                .weight(InternalIconsWeight)
                .clip(CircleShape)
                .height(UiConstants.ImageSize.large)
                .widthIn(max = UiConstants.ImageSize.large)
                .clickableWithHandCursor { previous() },
            color = contentColor
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        SoulIcon(
            icon = if (isPlaying) {
                CoreRes.drawable.ic_pause_filled
            } else {
                CoreRes.drawable.ic_play_filled
            },
            modifier = Modifier
                .weight(MainIconWeight)
                .clip(CircleShape)
                .height(UiConstants.Player.playerPlayerButtonSize)
                .widthIn(max = UiConstants.Player.playerPlayerButtonSize)
                .clickableWithHandCursor { togglePlayPause() },
            color = contentColor
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_next_filled,
            modifier = Modifier
                .weight(InternalIconsWeight)
                .clip(CircleShape)
                .height(UiConstants.ImageSize.large)
                .widthIn(max = UiConstants.ImageSize.large)
                .clickableWithHandCursor { next() },
            color = contentColor
        )
        Spacer(modifier = Modifier.weight(SpacerWeight))
        SoulIcon(
            icon = if (isMusicInFavorite) {
                CoreRes.drawable.ic_favorite_filled
            } else {
                CoreRes.drawable.ic_favorite
            },
            modifier = Modifier
                .weight(ExternalIconsWeight)
                .clip(CircleShape)
                .height(UiConstants.ImageSize.medium)
                .widthIn(max = UiConstants.ImageSize.medium)
                .clickableWithHandCursor { toggleFavoriteState() },
            color = contentColor
        )
    }
}

private const val SpacerWeight: Float = 0.2f
private const val ExternalIconsWeight: Float = 0.4f
private const val InternalIconsWeight: Float = 0.7f
private const val MainIconWeight: Float = 1f
