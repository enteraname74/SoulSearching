package com.github.enteraname74.soulsearching.coreui.slider

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulSlider(
    maxValue: Float,
    value: Float,
    onThumbDragged: (tmpPosition: Float?) -> Unit,
    onValueChanged: (newValue: Float) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    steps: Int = 0,
    sliderColors: SliderColors = SliderDefaults.colors(
        thumbColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        activeTrackColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        inactiveTrackColor = SoulSearchingColorTheme.colorScheme.secondary,
        activeTickColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        inactiveTickColor = SoulSearchingColorTheme.colorScheme.secondary,
    )
) {
    val fixedMin = max(0f, minValue)
    val fixedMax = max(maxValue, 0f)

    val interactionSource = remember { MutableInteractionSource() }


    var overridePosition: Float? by rememberSaveable {
        mutableStateOf(null)
    }

    Slider(
        modifier = modifier,
        value = overridePosition ?: value.coerceIn(fixedMin, fixedMax),
        onValueChange = { tmpPosition ->
            overridePosition = tmpPosition
            onThumbDragged(tmpPosition)
        },
        onValueChangeFinished = {
            overridePosition?.let { onValueChanged(it) }
            overridePosition = null
            onThumbDragged(null)
        },
        colors = sliderColors,
        valueRange = fixedMin..fixedMax,
        interactionSource = interactionSource,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                modifier = Modifier
                    .padding(
                        top = 2.dp,
                    ),
                thumbSize = DpSize(
                    width = UiConstants.ImageSize.verySmall,
                    height = UiConstants.ImageSize.verySmall
                ),
                colors = sliderColors
            )
        },
        steps = steps,
        track = {
            SliderDefaults.Track(
                sliderState = it,
                trackInsideCornerSize = 0.dp,
                drawStopIndicator = null,
                thumbTrackGapSize = 0.dp,
                modifier = Modifier.height(TrackHeight),
                colors = sliderColors,
            )
        }
    )
}

private val TrackHeight = 3.dp
