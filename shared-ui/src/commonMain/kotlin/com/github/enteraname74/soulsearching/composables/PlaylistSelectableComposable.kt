package com.github.enteraname74.soulsearching.composables

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
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun PlaylistSelectableComposable(
    playlist: Playlist,
    onClick: () -> Unit,
    isSelected: Boolean,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            SoulImage(
                coverId = playlist.coverId,
                size = UiConstants.CoverSize.small,
                tint = textColor
            )
            Text(
                text = playlist.name,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                onClick()
            },
            colors = CheckboxDefaults.colors(
                checkmarkColor = textColor,
                checkedColor = Color.Transparent,
                uncheckedColor = textColor
            )
        )
    }
}