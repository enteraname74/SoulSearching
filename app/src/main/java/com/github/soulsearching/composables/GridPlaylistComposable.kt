package com.github.soulsearching.composables

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.R
import com.github.soulsearching.database.model.Playlist

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridPlaylistComposable(
    image: Bitmap?,
    title: String,
    text: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }

            )

    ) {
        val modifierBase = Modifier
            .size(176.dp)
            .clip(RoundedCornerShape(percent = 4))

        if (image != null) {
            Image(
                modifier = modifierBase,
                bitmap = image.asImageBitmap(),
                contentDescription = stringResource(id = R.string.image),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = modifierBase,
                painter = painterResource(id = R.drawable.ic_saxophone_svg),
                contentDescription = stringResource(id = R.string.image),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}