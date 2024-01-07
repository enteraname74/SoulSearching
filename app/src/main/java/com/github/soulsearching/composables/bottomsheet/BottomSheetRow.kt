package com.github.soulsearching.composables.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun BottomSheetRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = DynamicColor.onSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(Constants.Spacing.large),
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(Constants.ImageSize.medium),
            imageVector = icon,
            contentDescription = text,
            colorFilter = ColorFilter.tint(textColor)
        )
        Text(
            text = text,
            color = textColor
        )
    }
}