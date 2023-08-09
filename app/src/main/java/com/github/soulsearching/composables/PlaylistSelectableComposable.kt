package com.github.soulsearching.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun PlaylistSelectableComposable(
    playlist: Playlist,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(Constants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            //AppImage(bitmap = playlist.playlistCover, size = 55.dp)
            Column(
                modifier = Modifier
                    .height(55.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = playlist.name,
                    color = DynamicColor.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "",
                    color = DynamicColor.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                onClick()
            },
            colors = CheckboxDefaults.colors(
                checkmarkColor = DynamicColor.onPrimary
            )
        )
    }
}