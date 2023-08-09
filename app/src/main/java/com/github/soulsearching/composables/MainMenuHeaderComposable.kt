package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
fun MainMenuHeaderComposable() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DynamicColor.primary)
            .padding(Constants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(Constants.ImageSize.medium),
            imageVector = Icons.Default.Menu,
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
            modifier = Modifier.size(Constants.ImageSize.medium),
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.shuffle_button_desc),
            tint = DynamicColor.onPrimary
        )
    }
}