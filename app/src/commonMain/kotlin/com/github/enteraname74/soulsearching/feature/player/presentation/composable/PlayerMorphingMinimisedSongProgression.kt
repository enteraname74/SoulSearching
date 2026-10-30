package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.ext.isMouseAndKeyboardOnly
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlaybackCommandsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewSettingsState

@Composable
internal fun PlayerMorphingMinimisedSongProgression(
    settingsState: PlayerViewSettingsState,
    playbackCommandsState: PlaybackCommandsState,
    currentMusicProgression: Float,
    currentMusicDuration: Float,
    onEnableSwipe: (enabled: Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val trackColor = SoulSearchingColorTheme.colorScheme.textSecondary.blend(
        other = SoulSearchingColorTheme.colorScheme.background,
        ratio = 0.5f,
    )

    AnimatedVisibility(
        modifier = Modifier.hoverable(interactionSource),
        visible = settingsState.isMinimisedSongProgressionShown && playbackCommandsState.seekTo != null
    ) {
        // Only go to slider mode if mouse/keyboard detected.
        if (isHovered && SoulPlatformUtils.isMouseAndKeyboardOnly()) {
            playbackCommandsState.seekTo?.let { seekTo ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    SoulSlider(
                        modifier = Modifier
                            // To recenter the slider
                            .offset(y = (-23).dp)
                            // To have a real full width progress bar, as the material progress one has some horizontal spacing.
                            .requiredWidth(maxWidth + 12.dp),
                        maxValue = currentMusicDuration,
                        value = currentMusicProgression,
                        onValueChanged = { seekTo(it.toInt()) },
                        onThumbDragged = {
                            onEnableSwipe(it == null)
                        },
                        sliderColors = SliderDefaults.colors(
                            thumbColor = SoulSearchingColorTheme.colorScheme.text,
                            activeTrackColor = SoulSearchingColorTheme.colorScheme.text,
                            inactiveTrackColor = trackColor,
                            activeTickColor = SoulSearchingColorTheme.colorScheme.text,
                            inactiveTickColor = trackColor,
                        )
                    )
                }
            }
        } else {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(1f - PlayerUiUtils.getAlphaTransition())
                    .height(2.dp),
                progress = {
                    (currentMusicProgression / currentMusicDuration).coerceIn(
                        0f,
                        1f
                    )
                },
                color = SoulSearchingColorTheme.colorScheme.text,
                trackColor = trackColor,
                drawStopIndicator = {},
                strokeCap = StrokeCap.Square,
                gapSize = 0.dp,
            )
        }
    }
}