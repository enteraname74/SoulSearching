package com.github.soulsearching.composables.playbuttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
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
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MinimisedPlayButtonsComposable(
    modifier: Modifier = Modifier,
    playerViewDraggableState: SwipeableState<BottomSheetStates>,
    playbackManager: PlaybackManager = injectElement(),
    playerViewModel: PlayerViewModel
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
                    if (playerViewDraggableState.currentValue == BottomSheetStates.MINIMISED) {
                        playbackManager.previous()
                    }
                },
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
        )
        if (playerViewModel.handler.isPlaying) {
            Image(
                imageVector = Icons.Rounded.Pause,
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        if (playerViewDraggableState.currentValue == BottomSheetStates.MINIMISED) {
                            playbackManager.togglePlayPause()
                        }
                    },
                colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
            )
        } else {
            Image(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        if (playerViewDraggableState.currentValue == BottomSheetStates.MINIMISED) {
                            playbackManager.togglePlayPause()
                        }
                    },
                colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
            )
        }
        Image(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    if (playerViewDraggableState.currentValue == BottomSheetStates.MINIMISED) {
                        playbackManager.next()
                    }
                },
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
        )
    }
}