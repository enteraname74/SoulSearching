package com.github.enteraname74.soulsearching.feature.settings.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SettingsActionElement(
    title: String,
    subTitle: String,
    clickAction: () -> Unit,
    isSelected: Boolean,
    padding: Dp = UiConstants.Spacing.veryLarge,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    subTextColor: Color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                clickAction()
            }
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.large)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = textColor,
                    style = UiConstants.Typography.bodyTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subTitle,
                    color = subTextColor,
                    style = UiConstants.Typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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