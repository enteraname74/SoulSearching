package com.github.enteraname74.soulsearching.coreui.slider

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulSlider(
    minValue: Float = 0f,
    maxValue: Float,
    value: Float,
    onValueChanged: (newValue: Float) -> Unit,
) {
    val fixedMin = max(0f, minValue)
    val fixedMax = max(maxValue, 0f)

    val interactionSource = remember { MutableInteractionSource() }
    val sliderColors = SliderDefaults.colors(
        thumbColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        activeTrackColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        inactiveTrackColor = SoulSearchingColorTheme.colorScheme.secondary,
    )

    Slider(
        modifier = Modifier
            .fillMaxWidth(),
        value = value.coerceIn(fixedMin, fixedMax),
        onValueChange = onValueChanged,
        colors = sliderColors,
        valueRange = 0f..fixedMax,
        interactionSource = interactionSource,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                modifier = Modifier
                    .size(UiConstants.ImageSize.small)
                    .padding(
                        start = 4.dp,
                        top = 4.dp
                    ),
                colors = sliderColors
            )
        }
    )
}