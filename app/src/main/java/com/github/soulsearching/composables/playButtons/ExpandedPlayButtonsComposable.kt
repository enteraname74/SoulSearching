package com.github.soulsearching.composables.playButtons

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.types.PlayerMode
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import java.util.*
import kotlin.collections.ArrayList


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun ExpandedPlayButtonsComposable(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    paddingBottom: Dp = 120.dp,
    mainColor: Color,
    sliderInactiveBarColor: Color,
    onMusicEvent: (MusicEvent) -> Unit,
    isMusicInFavorite: Boolean,
    playerMusicListViewModel: PlayerMusicListViewModel,
) {
    Column(
        modifier = Modifier
            .composed { modifier }
            .fillMaxWidth(widthFraction)
            .padding(
                start = 28.dp,
                end = 28.dp,
                bottom = paddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val interactionSource = remember { MutableInteractionSource() }
        val sliderColors = SliderDefaults.colors(
            thumbColor = mainColor,
            activeTrackColor = mainColor,
            inactiveTrackColor = sliderInactiveBarColor
        )

        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = PlayerUtils.playerViewModel.currentMusicPosition.toFloat(),
            onValueChange = {
                PlayerUtils.playerViewModel.currentMusicPosition = it.toInt()
                PlayerService.seekToPosition(it.toInt())
            },
            colors = sliderColors,
            valueRange = 0f..PlayerService.getMusicDuration().toFloat(),
            interactionSource = interactionSource,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .size(Constants.ImageSize.small)
                        .padding(
                            start = 4.dp,
                            top = 4.dp
                        ),
                    colors = sliderColors
                )
            }
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
                    color = mainColor,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = PlayerUtils.convertDuration(PlayerService.getMusicDuration()),
                    color = mainColor,
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
                    imageVector = when (PlayerUtils.playerViewModel.playerMode) {
                        PlayerMode.NORMAL -> Icons.Rounded.Sync
                        PlayerMode.SHUFFLE -> Icons.Rounded.Shuffle
                        PlayerMode.LOOP -> Icons.Rounded.Replay
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .size(Constants.ImageSize.medium)
                        .clickable {
                            PlayerUtils.playerViewModel.changePlayerMode()
                            playerMusicListViewModel.savePlayerMusicList(
                                PlayerUtils.playerViewModel.currentPlaylist.map { it.musicId } as ArrayList<UUID>
                            )
                        },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
                Image(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "",
                    modifier = Modifier
                        .size(Constants.ImageSize.large)
                        .clickable { PlayerService.playPrevious() },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
                if (PlayerUtils.playerViewModel.isPlaying) {
                    Image(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = "",
                        modifier = Modifier
                            .size(78.dp)
                            .clickable { PlayerUtils.playerViewModel.togglePlayPause() },
                        colorFilter = ColorFilter.tint(color = mainColor)
                    )
                } else {
                    Image(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier
                            .size(78.dp)
                            .clickable { PlayerUtils.playerViewModel.togglePlayPause() },
                        colorFilter = ColorFilter.tint(color = mainColor)
                    )
                }
                Image(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "",
                    modifier = Modifier
                        .size(Constants.ImageSize.large)
                        .clickable { PlayerService.playNext() },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
                Image(
                    imageVector = if (isMusicInFavorite) {
                        Icons.Rounded.Favorite
                    } else {
                        Icons.Rounded.FavoriteBorder
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .size(Constants.ImageSize.medium)
                        .clickable {
                            PlayerUtils.playerViewModel.currentMusic?.let {
                                onMusicEvent(
                                    MusicEvent.SetFavorite(
                                        musicId = it.musicId
                                    )
                                )
                            }
                        },
                    colorFilter = ColorFilter.tint(color = mainColor)
                )
            }
        }
    }
}