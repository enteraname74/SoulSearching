package com.github.soulsearching.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
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
import com.github.soulsearching.strings
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.utils.PlayerUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItemComposable(
    music: Music,
    onClick: (Music) -> Unit,
    onLongClick: () -> Unit,
    musicCover: ImageBitmap? = null,
    textColor: Color = DynamicColor.onPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(music) },
                onLongClick = onLongClick
            )
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
                    fontWeight = if (PlayerUtils.playerViewModel.handler.isSameMusic(music.musicId)) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (PlayerUtils.playerViewModel.handler.isSameMusic(music.musicId)) FontWeight.Bold else FontWeight.Normal
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