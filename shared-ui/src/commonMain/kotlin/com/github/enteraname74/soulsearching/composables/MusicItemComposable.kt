package com.github.enteraname74.soulsearching.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItemComposable(
    modifier: Modifier = Modifier,
    music: Music,
    onClick: (Music) -> Unit,
    onLongClick: () -> Unit,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    backgroundColor: Color = Color.Transparent,
    isPlayedMusic: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .combinedClickable(
                onClick = { onClick(music) },
                onLongClick = onLongClick
            )
            .padding(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoulImage(
                coverId = music.coverId,
                size = UiConstants.CoverSize.small,
                tint = textColor
            )
            Column(
                modifier = Modifier
                    .height(UiConstants.CoverSize.small)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music.name,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isPlayedMusic) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isPlayedMusic) FontWeight.Bold else FontWeight.Normal
                )

            }
            Icon(
                modifier = Modifier.clickable { onLongClick() },
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = strings.moreButton,
                tint = textColor
            )
        }
    }
}