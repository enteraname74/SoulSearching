package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun BottomSheetRow(
    icon: DrawableResource,
    text: String,
    onClick: () -> Unit,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithHandCursor {
                onClick()
            }
            .padding(
                vertical = UiConstants.Spacing.mediumPlus,
                horizontal = UiConstants.Spacing.large,
            ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SoulIcon(
            size = UiConstants.ImageSize.medium,
            icon = icon,
            contentDescription = text,
            color = textColor,
        )
        Text(
            text = text,
            color = textColor
        )
    }
}