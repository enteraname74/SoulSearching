package com.github.soulsearching.composables.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants

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