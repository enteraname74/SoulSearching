package com.github.enteraname74.soulsearching.composables.navigation

import androidx.compose.foundation.background
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun NavigationRow(
    navigationRowSpec: NavigationRowSpec,
    isSelected: Boolean,
) {
    // The margin lives on its own node so that the row below is exactly the clickable and highlighted area:
    // hover and press effects are drawn at the row's size and can't spill into the margin.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = UiConstants.Spacing.medium,
                vertical = UiConstants.Spacing.verySmall,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(UiConstants.Spacing.mediumPlus))
                .background(
                    color = if (isSelected) {
                        SoulSearchingColorTheme.colorScheme.surfaceHigh
                    } else {
                        Color.Transparent
                    }
                )
                .clickableWithHandCursor {
                    navigationRowSpec.onClick()
                }
                .padding(
                    horizontal = UiConstants.Spacing.mediumPlus,
                    vertical = UiConstants.Spacing.mediumPlus,
                ),
            horizontalArrangement = Arrangement.spacedBy(
                UiConstants.Spacing.large,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoulIcon(
                icon = navigationRowSpec.filledIcon,
                color = SoulSearchingColorTheme.colorScheme.text,
                contentDescription = null,
            )
            SoulText(
                text = navigationRowSpec.title,
                color = SoulSearchingColorTheme.colorScheme.text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            if ((navigationRowSpec as? NavigationRowSpec.Settings)?.isBadged == true) {
                Badge(
                    containerColor = SoulSearchingColorTheme.colorScheme.text,
                )
            }
        }
    }
}
