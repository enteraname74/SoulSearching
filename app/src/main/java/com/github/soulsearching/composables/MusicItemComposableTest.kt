package com.github.soulsearching.composables

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.reflect.KSuspendFunction1

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Suppress("UNUSED_PARAMETER", "UNUSED")
@Composable
fun MusicItemComposableTest(
    music: Music,
    onClick: (Music) -> Unit,
    onLongClick: () -> Unit,
    musicCover: Bitmap? = null,
    recoverMethod: KSuspendFunction1<UUID?, Bitmap?>
) {

    var test by rememberSaveable {
        mutableStateOf<Bitmap?>(null)
    }

    var isCoverFetched by rememberSaveable {
        mutableStateOf(false)
    }

    rememberCoroutineScope()
    if (!isCoverFetched) {
        Log.d("MUSIC ITEM", "WILL FETCH")
        DisposableEffect(key1 = music.musicId) {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = recoverMethod(music.coverId)
                test = bitmap
                isCoverFetched = true
            }
            onDispose {
                test?.recycle()
            }
        }
    }

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
            horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            AppImage(bitmap = test, size = 55.dp)
            Column(
                modifier = Modifier
                    .height(55.dp)
                    .width(200.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
        Icon(
            modifier = Modifier.clickable { onLongClick() },
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = stringResource(id = R.string.more_button),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}