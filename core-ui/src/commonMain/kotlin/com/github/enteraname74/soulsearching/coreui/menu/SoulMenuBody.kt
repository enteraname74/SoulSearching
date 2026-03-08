package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuBody(
    title: String,
    text: String?,
    modifier: Modifier = Modifier,
    titleMaxLines: Int = 1,
    textMaxLines: Int = 2,
    titleColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = titleColor,
            style = UiConstants.Typography.bodyTitle,
            maxLines = titleMaxLines,
            overflow = TextOverflow.Ellipsis
        )
        text?.let {
            Text(
                text = text,
                color = textColor,
                style = UiConstants.Typography.bodySmall,
                maxLines = textMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}