package com.github.soulsearching.composables


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.composables.playlistComposable.ColumnPlaylistPanel
import com.github.soulsearching.composables.playlistComposable.RowPlaylistPanel
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlaylistScreen(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    title: String,
    image: Bitmap?,
    navigateToModifyPlaylist: () -> Unit = {},
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    swipeableState: SwipeableState<BottomSheetStates>,
    playlistId: UUID?
) {
    val configuration = LocalConfiguration.current
    var max: Dp
    var start: Dp
    var center: Dp
    var end: Dp
    var centerPadding: Dp
    var musicListModifier: Modifier

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                max = this.maxWidth
                start = max / 2
                center = 80.dp
                end = max / 2
                centerPadding = end - center / 2
                musicListModifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
                    .width(end - (end - centerPadding))
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .padding(
                        start = Constants.Spacing.large,
                        end = Constants.Spacing.large
                    )

                TopPlaylistInformation(
                    title = title,
                    image = image,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.TopStart)
                        .width(start),
                    alignment = Alignment.TopStart,
                    navigateBack = navigateBack
                )
                ColumnPlaylistPanel(
                    modifier = Modifier
                        .padding(end = centerPadding)
                        .fillMaxHeight()
                        .width(center)
                        .align(Alignment.CenterEnd),
                    editAction = navigateToModifyPlaylist
                )
            }
            else -> {
                max = this.maxHeight
                start = max / 3
                center = 80.dp
                end = max * 2 / 3
                centerPadding = end - center / 2
                musicListModifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .height(end - (end - centerPadding))
                    .clip(RoundedCornerShape(topStart = 30f, topEnd = 30f))
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .padding(
                        top = Constants.Spacing.large,
                        start = Constants.Spacing.large,
                        end = Constants.Spacing.large
                    )

                TopPlaylistInformation(
                    title = title,
                    image = image,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .height(start),
                    alignment = Alignment.TopStart,
                    navigateBack = navigateBack
                )
                RowPlaylistPanel(
                    modifier = Modifier
                        .padding(bottom = centerPadding)
                        .fillMaxWidth()
                        .height(center)
                        .align(Alignment.BottomCenter),
                    editAction = navigateToModifyPlaylist
                )
            }
        }
        MusicList(
            musicState = musicState,
            playlistState = playlistState,
            onMusicEvent = onMusicEvent,
            onPlaylistEvent = onPlaylistEvent,
            navigateToModifyMusic = navigateToModifyMusic,
            modifier = musicListModifier,
            retrieveCoverMethod = { retrieveCoverMethod(it) },
            swipeableState = swipeableState,
            playlistId = playlistId
        )
    }
}

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun TopPlaylistInformation(
    title: String,
    image: Bitmap?,
    modifier: Modifier = Modifier,
    alignment: Alignment,
    navigateBack: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        if (image != null) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                bitmap = image.asImageBitmap(),
                contentDescription = stringResource(id = R.string.playlist_cover),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_saxophone_svg),
                contentDescription = stringResource(id = R.string.playlist_cover),
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }

        Image(
            modifier = Modifier
                .align(alignment)
                .padding(Constants.Spacing.medium)
                .size(Constants.ImageSize.medium)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6F), CircleShape)
                .clickable {
                    navigateBack()
                },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(id = R.string.playlist_cover),
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
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}