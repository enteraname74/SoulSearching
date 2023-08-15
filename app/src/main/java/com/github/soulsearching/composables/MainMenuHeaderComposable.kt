package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.ui.theme.DynamicColor

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
            contentDescription = stringResource(id = R.string.navigation_drawer_desc),
            tint = DynamicColor.onPrimary
        )
        Text(
            text = stringResource(id = R.string.app_name),
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
            contentDescription = stringResource(id = R.string.shuffle_button_desc),
            tint = DynamicColor.onPrimary
        )
    }
}