package com.github.soulsearching.composables.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsActionElement(
    title: String,
    text: String,
    clickAction: () -> Unit,
    isSelected: Boolean,
    padding: Dp = Constants.Spacing.veryLarge
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
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.large)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = DynamicColor.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = text,
                    color = DynamicColor.subText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        RadioButton(
            selected = isSelected,
            onClick = clickAction,
            colors = RadioButtonDefaults.colors(
                selectedColor = DynamicColor.onPrimary,
                unselectedColor = DynamicColor.onPrimary
            )
        )
    }
}