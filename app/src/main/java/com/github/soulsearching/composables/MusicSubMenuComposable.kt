package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Shuffle
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

@Composable
fun MusicSubMenuComposable(
    shuffleAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(Constants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.musics),
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        Icon(
            modifier = Modifier
                .size(Constants.ImageSize.medium)
                .clickable {
                    shuffleAction()
                },
            imageVector = Icons.Default.Shuffle,
            contentDescription = stringResource(id = R.string.search_button),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}