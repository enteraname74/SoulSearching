package com.github.enteraname74.soulsearching.coreui.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlinx.coroutines.launch

/**
 * Row of mutually exclusive choices.
 * The background of the selected choice slides to the newly selected one.
 */
@Composable
fun <T> SoulSegmentedChoices(
    choices: List<SoulSegmentedChoice<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    colors: SoulSegmentedChoicesColors = SoulSegmentedChoicesDefaults.colors(),
    contentPadding: PaddingValues = SoulSegmentedChoicesDefaults.contentPadding(),
) {
    if (choices.isEmpty()) return

    val density = LocalDensity.current
    val indicatorCornerRadius = with(density) { INDICATOR_CORNER_RADIUS.toPx() }

    val choiceBounds = remember { mutableStateMapOf<Int, ChoiceBounds>() }
    val indicatorX = remember { Animatable(0f) }
    val indicatorWidth = remember { Animatable(0f) }
    var isIndicatorPlaced by remember { mutableStateOf(false) }

    val selectedIndex = choices.indexOfFirst { it.value == selected }
    val targetBounds = choiceBounds[selectedIndex]

    LaunchedEffect(targetBounds) {
        if (targetBounds == null) return@LaunchedEffect

        if (isIndicatorPlaced) {
            launch { indicatorX.animateTo(targetBounds.x) }
            indicatorWidth.animateTo(targetBounds.width)
        } else {
            indicatorX.snapTo(targetBounds.x)
            indicatorWidth.snapTo(targetBounds.width)
            isIndicatorPlaced = true
        }
    }

    Row(
        modifier = modifier
            .clip(CONTAINER_SHAPE)
            .background(colors.containerColor)
            .padding(CONTAINER_PADDING)
            .drawBehind {
                if (!isIndicatorPlaced || selectedIndex == -1) return@drawBehind
                drawRoundRect(
                    color = colors.selectedContainerColor,
                    topLeft = Offset(x = indicatorX.value, y = 0f),
                    size = Size(width = indicatorWidth.value, height = size.height),
                    cornerRadius = CornerRadius(indicatorCornerRadius),
                )
            }
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CHOICES_SPACING),
    ) {
        choices.forEachIndexed { index, choice ->
            val isSelected = index == selectedIndex
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) colors.selectedContentColor else colors.contentColor,
            )

            Box(
                modifier = Modifier
                    .onPlaced { coordinates ->
                        choiceBounds[index] = ChoiceBounds(
                            x = coordinates.positionInParent().x,
                            width = coordinates.size.width.toFloat(),
                        )
                    }
                    .clip(RoundedCornerShape(INDICATOR_CORNER_RADIUS))
                    .pointerHoverIcon(PointerIcon.Hand)
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelect(choice.value) },
                    )
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                SoulText(
                    text = choice.label,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

data class SoulSegmentedChoice<T>(
    val value: T,
    val label: SoulTextSpec,
)

data class SoulSegmentedChoicesColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
)

object SoulSegmentedChoicesDefaults {
    @Composable
    fun colors(
        containerColor: Color = SoulSearchingColorTheme.colorScheme.surface,
        selectedContainerColor: Color = SoulSearchingColorTheme.colorScheme.surfaceHigh,
        contentColor: Color = SoulSearchingColorTheme.colorScheme.textSecondary,
        selectedContentColor: Color = SoulSearchingColorTheme.colorScheme.text,
    ): SoulSegmentedChoicesColors = SoulSegmentedChoicesColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
    )

    fun contentPadding(
        vertical: Dp = UiConstants.Spacing.medium,
        horizontal: Dp = UiConstants.Spacing.large,
    ): PaddingValues = PaddingValues(
        vertical = vertical,
        horizontal = horizontal,
    )
}

private data class ChoiceBounds(
    val x: Float,
    val width: Float,
)

private val CONTAINER_SHAPE = RoundedCornerShape(12.dp)
private val CONTAINER_PADDING: Dp = UiConstants.Spacing.small
private val INDICATOR_CORNER_RADIUS: Dp = 9.dp
private val CHOICES_SPACING: Dp = UiConstants.Spacing.small
