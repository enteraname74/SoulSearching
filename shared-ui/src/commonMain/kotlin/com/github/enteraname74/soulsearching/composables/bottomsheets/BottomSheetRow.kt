package com.github.soulsearching.composables.bottomsheets

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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun BottomSheetRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(UiConstants.Spacing.large),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(UiConstants.ImageSize.medium),
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