package com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols

import androidx.compose.foundation.Image
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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates

@Composable
fun MinimisedPlayerControlsComposable(
    modifier: Modifier = Modifier,
    playerViewState: BottomSheetStates,
    isPlaying: Boolean,
    previous: () -> Unit,
    togglePlayPause: () -> Unit,
    next: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
    ) {
        Image(
            imageVector = Icons.Rounded.SkipPrevious,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickableIf(enabled = playerViewState == BottomSheetStates.MINIMISED) {
                    previous()
                },
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
        )
        Image(
            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickableIf(enabled = playerViewState == BottomSheetStates.MINIMISED) {
                    togglePlayPause()
                },
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
        )
        Image(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickableIf(enabled = playerViewState == BottomSheetStates.MINIMISED) {
                    next()
                },
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onSecondary)
        )
    }
}