package com.github.enteraname74.soulsearching.coreui.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulText(
    text: String,
    color: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    style: TextStyle = UiConstants.Typography.body,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style,
    )
}