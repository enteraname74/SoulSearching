package com.github.soulsearching.composables

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.ui.theme.DynamicColor
import java.util.*

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItemComposable(
    music: Music,
    onClick: (Music) -> Unit,
    onLongClick: () -> Unit,
    musicCover: Bitmap? = null,
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
                    fontWeight = if (PlayerUtils.playerViewModel.isSameMusic(music.musicId)) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (PlayerUtils.playerViewModel.isSameMusic(music.musicId)) FontWeight.Bold else FontWeight.Normal
                )

            }
            Icon(
                modifier = Modifier.clickable { onLongClick() },
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.more_button),
                tint = textColor
            )
        }
    }
}