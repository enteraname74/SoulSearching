package com.github.enteraname74.soulsearching.feature.player.presentation.screen.views

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.ExpandedPlayerControlsComposable

@Composable
internal fun PlayerSwipeableDefaultMobileView(
    currentMusicProgression: Int,
    state: PlayerViewState.Data,
    playbackCommandsState: PlaybackCommandsState,
    onMaxWidthForCover: (maxWidth: Dp) -> Unit,
    onMaxHeightForCover: (maxHeight: Dp) -> Unit,
) {
    val draggablePanelHeight = PlayerUiUtils.getDraggablePanelCollapsedOffset().toDp()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(
                    vertical = UiConstants.Spacing.large,
                )
                .weight(1f)
        ) {
            LaunchedEffect(maxWidth) {
                onMaxWidthForCover(maxWidth)
            }
            LaunchedEffect(maxHeight) {
                onMaxHeightForCover(maxHeight)
            }
        }
        ExpandedPlayerControlsComposable(
            modifier = Modifier
                .padding(
                    start = UiConstants.Spacing.veryLarge,
                    end = UiConstants.Spacing.veryLarge,
                    bottom = 50.dp,
                ),
            currentMusicProgression = currentMusicProgression,
            state = state,
            playbackCommandsState = playbackCommandsState,
        )

        Spacer(modifier = Modifier.height(draggablePanelHeight))
    }
}