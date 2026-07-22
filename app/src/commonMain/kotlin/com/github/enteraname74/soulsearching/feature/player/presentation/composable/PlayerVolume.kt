package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_volume_down
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_volume_mute
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_volume_up
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState

@Composable
fun PlayerVolume(
    playbackCommandsState: PlaybackCommandsState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
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