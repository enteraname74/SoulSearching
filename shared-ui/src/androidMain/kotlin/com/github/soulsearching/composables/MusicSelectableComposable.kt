package com.github.soulsearching.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.Constants
import com.github.soulsearching.theme.DynamicColor

@Composable
fun MusicSelectableComposable(
    music: Music,
    onClick: () -> Unit,
    musicCover: ImageBitmap?,
    isSelected: Boolean,
    textColor: Color = DynamicColor.onPrimary
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppImage(
                bitmap = musicCover,
                size = 55.dp,
                tint = textColor
            )
            Column(
                modifier = Modifier
                    .height(55.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music.name,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal
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
}