package com.github.soulsearching.composables.playButtons

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.service.PlayerService


@Composable
fun ExpandedPlayButtonsComposable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 28.dp,
                end = 28.dp,
                bottom = 120.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = PlayerUtils.playerViewModel.currentMusicPosition.toFloat(),
            onValueChange = {
                PlayerUtils.playerViewModel.currentMusicPosition = it.toInt()
                PlayerService.seekToPosition(it.toInt())
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onSecondary,
                activeTrackColor = MaterialTheme.colorScheme.onSecondary,
            ),
            valueRange = 0f..PlayerService.getMusicDuration().toFloat()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = PlayerUtils.convertDuration(PlayerUtils.playerViewModel.currentMusicPosition),
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = PlayerUtils.convertDuration(PlayerService.getMusicDuration()),
                    color = MaterialTheme.colorScheme.onSecondary,
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
                        .clickable { PlayerService.playPrevious() },
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                )
                if (PlayerUtils.playerViewModel.isPlaying) {
                    Image(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = "",
                        modifier = Modifier
                            .size(78.dp)
                            .clickable { PlayerUtils.playerViewModel.setPlayingState() },
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                    )
                } else {
                    Image(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier
                            .size(78.dp)
                            .clickable { PlayerUtils.playerViewModel.setPlayingState() },
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
                    )
                }
                Image(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "",
                    modifier = Modifier
                        .size(Constants.ImageSize.large)
                        .clickable { PlayerService.playNext() },
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
    }
}