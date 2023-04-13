package com.github.soulsearching.composables.screens


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.ModifyPlaylistActivity
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.MusicList
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.SelectedPlaylistState

@Composable
fun PlaylistScreen(
    selectedPlaylistState: SelectedPlaylistState,
    musicState: MusicState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit
) {
    val configuration = LocalConfiguration.current
    val activity = LocalContext.current as Activity

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                val maxWidth = this.maxWidth
                val startWidth = maxWidth / 2
                val centerWidth = 80.dp
                val endWidth = maxWidth / 2
                val centerPaddingEnd = endWidth - centerWidth / 2

                TopPlaylistInformation(
                    selectedPlaylistState = selectedPlaylistState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.TopStart)
                        .width(startWidth),
                    alignment = Alignment.TopStart
                )
                ColumnPlaylistPanel(
                    modifier = Modifier
                        .padding(end = centerPaddingEnd)
                        .fillMaxHeight()
                        .width(centerWidth)
                        .align(Alignment.CenterEnd),
                    editAction = {
                        val intent = Intent(activity, ModifyPlaylistActivity::class.java)
                        intent.putExtra(
                            "playlistId",
                            selectedPlaylistState.playlistWithMusics!!.playlist.playlistId.toString()
                        )
                        activity.startActivity(intent)
                    }
                )
                MusicList(
                    state = musicState,
                    onEvent = onMusicEvent,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.TopEnd)
                        .width(endWidth - (endWidth - centerPaddingEnd))
                        .background(color = MaterialTheme.colorScheme.secondary)
                        .padding(Constants.Spacing.large)
                )
            }
            else -> {
                val maxHeight = this.maxHeight
                val topHeight = maxHeight / 3
                val centerHeight = 80.dp
                val bottomHeight = maxHeight * 2 / 3
                val centerPaddingBottom = bottomHeight - centerHeight / 2

                TopPlaylistInformation(
                    selectedPlaylistState = selectedPlaylistState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .height(topHeight),
                    alignment = Alignment.TopStart
                )
                RowPlaylistPanel(
                    modifier = Modifier
                        .padding(bottom = centerPaddingBottom)
                        .fillMaxWidth()
                        .height(centerHeight)
                        .align(Alignment.BottomCenter),
                    editAction = {
                        val intent = Intent(activity, ModifyPlaylistActivity::class.java)
                        intent.putExtra(
                            "playlistId",
                            selectedPlaylistState.playlistWithMusics!!.playlist.playlistId.toString()
                        )
                        activity.startActivity(intent)
                    }
                )
                MusicList(
                    state = musicState,
                    onEvent = onMusicEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .height(bottomHeight - (bottomHeight - centerPaddingBottom))
                        .clip(RoundedCornerShape(topStart = 30f, topEnd = 30f))
                        .background(color = MaterialTheme.colorScheme.secondary)
                        .padding(Constants.Spacing.large)
                )
            }
        }
    }
}

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun TopPlaylistInformation(
    selectedPlaylistState: SelectedPlaylistState,
    modifier: Modifier = Modifier,
    alignment: Alignment
) {

    val activity = LocalContext.current as Activity

    Box(
        modifier = modifier
    ) {
        if (selectedPlaylistState.playlistWithMusics != null) {
            if (selectedPlaylistState.playlistWithMusics!!.playlist.playlistCover != null) {
                Log.d("Image bitmap : ", selectedPlaylistState.playlistWithMusics!!.playlist.playlistCover!!.toString())
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    bitmap = selectedPlaylistState.playlistWithMusics!!.playlist.playlistCover!!.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_saxophone_svg),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

        Image(
            modifier = Modifier
                .align(alignment)
                .padding(Constants.Spacing.medium)
                .size(Constants.ImageSize.medium)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6F), CircleShape)
                .clickable {
                    activity.finish()
                },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6F)
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        bottom = 40.dp,
                        start = Constants.Spacing.medium,
                        top = Constants.Spacing.medium
                    ),
                text = if (selectedPlaylistState.playlistWithMusics != null) selectedPlaylistState.playlistWithMusics!!.playlist.name else "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun ColumnPlaylistPanel(
    modifier: Modifier = Modifier,
    editAction: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxHeight()
        .composed { modifier }
        .clip(RoundedCornerShape(30.dp))
        .padding(Constants.Spacing.medium)
        .background(MaterialTheme.colorScheme.secondary)
        .padding(Constants.Spacing.medium),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagesButton(
            editAction = editAction
        )
    }
}

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun RowPlaylistPanel(
    modifier: Modifier = Modifier,
    editAction: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .composed { modifier }
        .clip(RoundedCornerShape(30.dp))
        .padding(Constants.Spacing.medium)
        .background(MaterialTheme.colorScheme.secondary)
        .padding(Constants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImagesButton(
            editAction = editAction
        )
    }
}

@Composable
fun ImagesButton(
    editAction: () -> Unit
) {
    Image(
        modifier = Modifier
            .padding(Constants.Spacing.medium)
            .size(Constants.ImageSize.medium)
            .clickable {
                editAction()
            },
        imageVector = Icons.Default.Edit,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
    )
    Image(
        modifier = Modifier.size(Constants.ImageSize.medium),
        imageVector = Icons.Default.PlaylistAdd,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
    )
    Image(
        modifier = Modifier.size(Constants.ImageSize.medium),
        imageVector = Icons.Default.Shuffle,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
    )
}