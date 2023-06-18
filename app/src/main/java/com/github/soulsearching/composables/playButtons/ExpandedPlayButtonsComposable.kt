package com.github.soulsearching.composables.playButtons

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.service.PlayerService


@Composable
fun ExpandedPlayButtonsComposable(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.large)
    ) {
        Image(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = "",
            modifier = Modifier
                .size(Constants.ImageSize.medium)
                .clickable { },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
        )
        Image(
            imageVector = Icons.Rounded.SkipPrevious,
            contentDescription = "",
            modifier = Modifier
                .size(Constants.ImageSize.large)
                .clickable {
                    PlayerUtils.playerViewModel.setPreviousMusic()
                    PlayerService.playPrevious(context)
                },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
        )
        if (PlayerUtils.playerViewModel.isPlaying) {
            Image(
                imageVector = Icons.Rounded.Pause,
                contentDescription = "",
                modifier = Modifier
                    .size(78.dp)
                    .clickable { PlayerUtils.playerViewModel.setPlayingState(context) },
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
            )
        } else {
            Image(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "",
                modifier = Modifier
                    .size(78.dp)
                    .clickable { PlayerUtils.playerViewModel.setPlayingState(context) },
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
            )
        }
        Image(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = "",
            modifier = Modifier
                .size(Constants.ImageSize.large)
                .clickable {
                    PlayerUtils.playerViewModel.setNextMusic()
                    PlayerService.playNext(context)
                },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
        )
        Image(
            imageVector = Icons.Rounded.FavoriteBorder,
            contentDescription = "",
            modifier = Modifier
                .size(Constants.ImageSize.medium)
                .clickable { },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
        )
    }
}