package com.github.soulsearching.composables.playButtons

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.types.BottomSheetStates
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.theme.DynamicColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinimisedPlayButtonsComposable(
    modifier: Modifier = Modifier,
    playerViewDraggableState: PlayerDraggableState
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
    ) {
        Image(
            imageVector = Icons.Rounded.SkipPrevious,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    if (playerViewDraggableState.state.currentValue == BottomSheetStates.MINIMISED) {
                        PlayerService.playPrevious()
                    }
                },
            colorFilter = ColorFilter.tint(color = DynamicColor.onSecondary)
        )
        if (PlayerUtils.playerViewModel.isPlaying) {
            Image(
                imageVector = Icons.Rounded.Pause,
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        if (playerViewDraggableState.state.currentValue == BottomSheetStates.MINIMISED) {
                            PlayerUtils.playerViewModel.togglePlayPause()
                        }
                    },
                colorFilter = ColorFilter.tint(color = DynamicColor.onSecondary)
            )
        } else {
            Image(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        if (playerViewDraggableState.state.currentValue == BottomSheetStates.MINIMISED) {
                            PlayerUtils.playerViewModel.togglePlayPause()
                        }
                    },
                colorFilter = ColorFilter.tint(color = DynamicColor.onSecondary)
            )
        }
        Image(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    if (playerViewDraggableState.state.currentValue == BottomSheetStates.MINIMISED) {
                        PlayerService.playNext()
                    }
                },
            colorFilter = ColorFilter.tint(color = DynamicColor.onSecondary)
        )
    }
}