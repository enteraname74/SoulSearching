package com.github.soulsearching.composables.playlistComposable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.github.soulsearching.Constants

@Composable
fun ImagesButton(
    editAction: () -> Unit,
    shuffleAction: () -> Unit
) {
    Image(
        modifier = Modifier
            .padding(Constants.Spacing.medium)
            .size(Constants.ImageSize.medium)
            .clickable {
                editAction()
            },
        imageVector = Icons.Default.Edit,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
    )
    Image(
        modifier = Modifier.size(Constants.ImageSize.medium),
        imageVector = Icons.Default.PlaylistAdd,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
    )
    Image(
        modifier = Modifier
            .size(Constants.ImageSize.medium)
            .clickable { shuffleAction() },
        imageVector = Icons.Default.Shuffle,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
    )
}