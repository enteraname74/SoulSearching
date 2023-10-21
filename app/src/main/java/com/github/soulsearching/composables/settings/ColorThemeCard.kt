package com.github.soulsearching.composables.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun ColorThemeCard(
    title: String,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    padding: Dp = Constants.Spacing.large,
    firstImageId: Int,
    secondImageId: Int,
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
                painter = painterResource(firstImageId),
                contentDescription = ""
            )
            Image(
                modifier = Modifier
                    .size(120.dp, 360.dp),
                painter = painterResource(secondImageId),
                contentDescription = ""
            )
        }
    }
}