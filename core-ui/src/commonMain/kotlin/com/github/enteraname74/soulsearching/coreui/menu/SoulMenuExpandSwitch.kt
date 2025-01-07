package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuExpandSwitch(
    title: String,
    subTitle: String?,
    clickAction: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    padding: Dp = UiConstants.Spacing.large,
    containerColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    subTextColor: Color = SoulSearchingColorTheme.colorScheme.subSecondaryText,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(size = RoundedCornerShapeValue)
            )
    ) {
        SoulMenuSwitch(
            title = title,
            subTitle = subTitle,
            titleColor = textColor,
            textColor = subTextColor,
            toggleAction = clickAction,
            isChecked = isExpanded,
            padding = PaddingValues(
                all = padding,
            )
        )
        AnimatedVisibility(
            visible = isExpanded
        ) {
            content()
        }
    }
}

private val RoundedCornerShapeValue: Dp = 10.dp