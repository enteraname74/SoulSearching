package com.github.soulsearching.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.soulsearching.Constants
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun PlaylistSelectableComposable(
    playlist: Playlist,
    onClick: () -> Unit,
    isSelected: Boolean,
    textColor: Color = DynamicColor.onSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(Constants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = playlist.name,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                onClick()
            },
            colors = CheckboxDefaults.colors(
                checkmarkColor = DynamicColor.onPrimary,
                checkedColor = Color.Transparent,

            )
        )
    }
}