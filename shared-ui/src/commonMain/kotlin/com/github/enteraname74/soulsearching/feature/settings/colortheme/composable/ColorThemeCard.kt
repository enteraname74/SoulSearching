package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ColorThemeCard(
    title: String,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = UiConstants.Spacing.large,
    firstImage: DrawableResource,
    secondImage: DrawableResource,
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
                painter = painterResource(firstImage),
                contentDescription = ""
            )
            Image(
                modifier = Modifier
                    .size(120.dp, 360.dp),
                painter = painterResource(secondImage),
                contentDescription = ""
            )
        }
    }
}