package com.github.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.github.soulsearching.Constants
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsElement(
    title: String,
    text: String,
    clickAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Constants.Spacing.large,
                end = Constants.Spacing.large,
                top = Constants.Spacing.medium,
                bottom = Constants.Spacing.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
    ) {
        Image(
            modifier = Modifier.size(Constants.ImageSize.large),
            imageVector = Icons.Rounded.Add,
            contentDescription = "",
            colorFilter = ColorFilter.tint(DynamicColor.onPrimary)
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = DynamicColor.onPrimary
            )
            Text(
                text = text,
                color = DynamicColor.subText
            )
        }
    }
}