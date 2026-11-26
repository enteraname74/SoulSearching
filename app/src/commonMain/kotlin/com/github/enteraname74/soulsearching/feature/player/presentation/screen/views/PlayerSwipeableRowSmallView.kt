package com.github.enteraname74.soulsearching.feature.player.presentation.screen.views

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.ExpandedPlayerControlsComposable

@Composable
internal fun PlayerSwipeableRowSmallView(
    currentMusicProgression: Int,
    state: PlayerViewState.Data,
    playbackCommandsState: PlaybackCommandsState,
    onMaxWidthForCover: (maxWidth: Dp) -> Unit,
    onMaxHeightForCover: (maxHeight: Dp) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(
                start = UiConstants.Spacing.huge,
                end = UiConstants.Spacing.huge,
                bottom = UiConstants.Spacing.huge,
            )
            .alpha(PlayerUiUtils.getTransitionRatio()),
    ) {
        LaunchedEffect(maxWidth) {
            onMaxWidthForCover(maxWidth / 2)
        }
        LaunchedEffect(maxHeight) {
            onMaxHeightForCover(maxHeight)
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Spacer for player dynamic cover view
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            ExpandedPlayerControlsComposable(
                modifier = Modifier
                    .weight(1f),
                currentMusicProgression = currentMusicProgression,
                state = state,
                playbackCommandsState = playbackCommandsState,
            )
        }
    }
}