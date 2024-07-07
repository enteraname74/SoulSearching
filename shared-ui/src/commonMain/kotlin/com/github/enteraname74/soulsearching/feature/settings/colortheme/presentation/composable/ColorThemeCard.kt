package com.github.enteraname74.soulsearching.feature.settings.colortheme.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext

@Composable
fun ColorThemeCard(
    title: String,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = UiConstants.Spacing.large,
    firstImagePath: String,
    secondImagePath: String,
) {
    ColorCard(
        title = title,
        text = text,
        onClick = onClick,
        isSelected = isSelected,
        padding = padding
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                modifier = Modifier
                    .size(120.dp, 360.dp),
                painter = SoulSearchingContext.appPainterResource(resourcePath = firstImagePath),
                contentDescription = ""
            )
            Image(
                modifier = Modifier
                    .size(120.dp, 360.dp),
                painter = SoulSearchingContext.appPainterResource(resourcePath = secondImagePath),
                contentDescription = ""
            )
        }
    }
}