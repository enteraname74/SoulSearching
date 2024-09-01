package com.github.enteraname74.soulsearching.feature.settings.colortheme.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    images: List<DrawableResource>,
) {
    ColorCard(
        title = title,
        text = text,
        onClick = onClick,
        isSelected = isSelected,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            images.forEach {
                Image(
                    modifier = Modifier
                        .size(120.dp, 360.dp),
                    painter = painterResource(it),
                    contentDescription = ""
                )
            }
        }
    }
}