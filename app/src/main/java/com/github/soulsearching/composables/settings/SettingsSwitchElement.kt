package com.github.soulsearching.composables.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsSwitchElement(
    title: String,
    text: String = "",
    titleFontSize: TextUnit = 18.sp,
    toggleAction: () -> Unit,
    isChecked: Boolean,
    padding: PaddingValues = PaddingValues(Constants.Spacing.veryLarge),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                toggleAction()
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
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Constants.Spacing.small),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = DynamicColor.onPrimary,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight(500),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        color = DynamicColor.subText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { toggleAction() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = DynamicColor.subText,
                checkedTrackColor = DynamicColor.onPrimary,
                checkedBorderColor = DynamicColor.onPrimary,
                uncheckedThumbColor = DynamicColor.subText,
                uncheckedTrackColor = Color.Transparent,
                uncheckedBorderColor = DynamicColor.subText,
            )
        )
    }
}