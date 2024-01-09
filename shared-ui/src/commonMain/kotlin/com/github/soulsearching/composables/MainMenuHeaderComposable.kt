package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.soulsearching.Constants
import com.github.soulsearching.strings
import com.github.soulsearching.theme.DynamicColor

@Composable
fun MainMenuHeaderComposable(
    settingsAction: () -> Unit,
    searchAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DynamicColor.primary)
            .padding(
                start = Constants.Spacing.medium,
                end = Constants.Spacing.medium,
                top = Constants.Spacing.small,
                bottom = Constants.Spacing.small
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(Constants.ImageSize.medium)
                .clickable {
                    settingsAction()
                },
            imageVector = Icons.Rounded.Settings,
            contentDescription = strings.settingsAccessButton,
            tint = DynamicColor.onPrimary
        )
        Text(
            text = strings.appName,
            color = DynamicColor.onPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Icon(
            modifier = Modifier
                .size(Constants.ImageSize.medium)
                .clickable {
                    searchAction()
                },
            imageVector = Icons.Rounded.Search,
            contentDescription = strings.shuffleButton,
            tint = DynamicColor.onPrimary
        )
    }
}