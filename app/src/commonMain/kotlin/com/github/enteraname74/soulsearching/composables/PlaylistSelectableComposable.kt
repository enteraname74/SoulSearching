package com.github.enteraname74.soulsearching.composables

import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulCheckBox
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun PlaylistSelectableComposable(
    modifier: Modifier = Modifier,
    playlistWithMusics: PlaylistWithMusics,
    onClick: () -> Unit,
    isSelected: Boolean,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableWithHandCursor {
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
                cover = playlistWithMusics.cover,
                size = UiConstants.CoverSize.small,
                tint = textColor
            )
            SoulText(
                text = playlistWithMusics.playlist.name.toTextSpec(),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        SoulCheckBox(
            checked = isSelected,
            onCheckedChange = {
                onClick()
            },
            color = textColor,
        )
    }
}
