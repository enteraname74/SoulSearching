package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.ext.disabledIfNoAction

@Composable
fun MinimisedPlayerControlsComposable(
    modifier: Modifier = Modifier,
    playerViewState: BottomSheetStates,
    playbackCommandsState: PlaybackCommandsState,
) {
    val isMinimised = playerViewState == BottomSheetStates.MINIMISED

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
    ) {
        if (PlayerUiUtils.canShowSidePanel()) {
            PlayerVolume(
                playbackCommandsState = playbackCommandsState,
            )
        }
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_previous_filled,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .optionalClickable(playbackCommandsState.previous.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.previous)
        )
        SoulIcon(
            icon = if (playbackCommandsState.isPlaying) {
                CoreRes.drawable.ic_pause_filled
            } else {
                CoreRes.drawable.ic_play_filled
            },
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .optionalClickable(playbackCommandsState.togglePlayPause.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.togglePlayPause)
        )
        SoulIcon(
            icon = CoreRes.drawable.ic_skip_next_filled,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .optionalClickable(playbackCommandsState.next.takeIf { isMinimised }),
            color = SoulSearchingColorTheme.colorScheme.onSecondary.disabledIfNoAction(playbackCommandsState.next)
        )
    }
}


@Composable
private fun PlayerVolume(
    playbackCommandsState: PlaybackCommandsState,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoulIcon(
            icon = if (playbackCommandsState.playerVolume > .5f) {
                CoreRes.drawable.ic_volume_up
            } else {
                CoreRes.drawable.ic_volume_down
            }
        )
        SoulSlider(
            modifier = Modifier
                .width(200.dp),
            minValue = 1f,
            maxValue = 10f,
            steps = 8,
            value = playbackCommandsState.playerVolume * 10,
            sliderColors = SliderDefaults.colors(
                thumbColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                activeTrackColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                inactiveTrackColor = SoulSearchingColorTheme.colorScheme.primary,
                activeTickColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                inactiveTickColor = SoulSearchingColorTheme.colorScheme.primary,
            ),
            onThumbDragged = { playerVolume ->
                playerVolume?.let {
                    val fixedVolume = (it / 10).coerceIn(0.1f, 1f)
                    playbackCommandsState.setPlayerVolume(fixedVolume)
                }
            },
            onValueChanged = {
                // update done on drag
            }
        )
    }
}