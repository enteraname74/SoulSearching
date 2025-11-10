package com.github.enteraname74.soulsearching.coreui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulMenuAction(
    title: String,
    subTitle: String?,
    clickAction: () -> Unit,
    clickEnabled: Boolean = true,
    isSelected: Boolean,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.veryLarge),
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    subTextColor: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableIf(enabled = clickEnabled) {
                clickAction()
            }
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
        ) {
            SoulMenuBody(
                title = title,
                text = subTitle,
                titleColor = textColor,
                textColor = subTextColor,
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = clickAction,
            colors = RadioButtonDefaults.colors(
                selectedColor = textColor,
                unselectedColor = textColor,
            )
        )
    }
}