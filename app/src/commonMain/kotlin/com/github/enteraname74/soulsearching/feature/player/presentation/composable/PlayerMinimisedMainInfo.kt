package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_volume_down
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_volume_up
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_volume_mute
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.MinimisedPlayerControlsComposable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerMinimisedMainInfo(
    imageSize: Dp,
    playerViewManager: PlayerViewManager = injectElement(),
    currentMusic: Music,
    alphaTransition: Float,
    playbackCommandsState: PlaybackCommandsState,
    state: PlayerViewState.Data,
) {
    Row(
        modifier = Modifier
            .height(imageSize)
            .fillMaxWidth()
            .padding(
                top = UiConstants.Spacing.medium,
                start = imageSize + UiConstants.Spacing.large,
                end = UiConstants.Spacing.small
            )
            .alpha(alphaTransition),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = currentMusic.name,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                maxLines = 1,
                fontSize = 15.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = currentMusic.artistsNames,
                color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        MinimisedPlayerControlsComposable(
            playerViewState = playerViewManager.draggableState.currentValue,
            playbackCommandsState = playbackCommandsState,
            state = state,
        )
        if (PlayerUiUtils.canShowSidePanel()) {
            PlayerVolume(
                playbackCommandsState = playbackCommandsState,
            )
        }
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
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            icon = when {
                playbackCommandsState.playerVolume == 0f -> CoreRes.drawable.ic_volume_mute
                playbackCommandsState.playerVolume <= 0.5f -> CoreRes.drawable.ic_volume_down
                else -> CoreRes.drawable.ic_volume_up
            }
        )
        SoulSlider(
            modifier = Modifier
                .width(150.dp),
            minValue = 0f,
            maxValue = 10f,
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
                    val fixedVolume = (it / 10).coerceIn(0f, 1f)
                    playbackCommandsState.setPlayerVolume(fixedVolume)
                }
            },
            onValueChanged = {
                // update done on drag
            }
        )
    }
}