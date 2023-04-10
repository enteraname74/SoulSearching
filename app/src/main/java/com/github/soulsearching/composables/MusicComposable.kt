package com.github.soulsearching.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.R

@Composable
fun MusicComposable(music: Music) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (music.albumCover != null) {
            Image(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp),
                bitmap = music.albumCover!!.asImageBitmap(),
                contentDescription = ""
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_saxophone_svg),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = music.name,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "{${music.artist}} | {${music.album}",
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}