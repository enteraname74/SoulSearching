package com.github.soulsearching.composables.screens


import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.bottomSheets.MusicFileBottomSheet
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.SelectedPlaylistState
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    selectedPlaylistState: SelectedPlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        val maxHeight = this.maxHeight
        val topHeight = maxHeight / 3
        val centerHeight = 80.dp
        val bottomHeight = maxHeight * 2 / 3
        val centerPaddingBottom = bottomHeight - centerHeight / 2

        TopPlaylistInformation(
            selectedPlaylistState = selectedPlaylistState,
            modifier = Modifier
                .align(Alignment.TopStart)
                .height(topHeight)
        )
        PlaylistPanel(
            modifier = Modifier
                .padding(bottom = centerPaddingBottom)
                .fillMaxWidth()
                .height(centerHeight)
                .align(Alignment.BottomCenter)
        )
        PlaylistMusicList(
            selectedPlaylistState = selectedPlaylistState,
            onEvent = onMusicEvent,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(bottomHeight - (bottomHeight - centerPaddingBottom))
        )
    }
}

@Composable
fun TopPlaylistInformation(
    selectedPlaylistState : SelectedPlaylistState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .composed { modifier }
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.ic_saxophone_svg),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        )
        Image(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Constants.Spacing.medium)
                .size(Constants.ImageSize.medium)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
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

@Composable
fun PlaylistPanel(
    modifier: Modifier = Modifier
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
        Image(
            modifier = Modifier.size(Constants.ImageSize.medium),
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
}

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlaylistMusicList(
    selectedPlaylistState: SelectedPlaylistState,
    onEvent: (MusicEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            MusicFileBottomSheet(
                modifyAction = {
                    /*
                    val intent = Intent(context, ModifyMusicActivity::class.java)
                    intent.putExtra(
                        "musicId",
                        state.selectedMusic!!.musicId.toString()
                    )
                    context.startActivity(intent)
                    coroutineScope.launch { modalSheetState.hide() }

                     */
                },
                removeAction = {
                    onEvent(MusicEvent.DeleteMusic)
                    coroutineScope.launch { modalSheetState.hide() }
                },
                addToPlaylistAction = {}
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .composed { modifier }
                .clip(RoundedCornerShape(topStart = 30f, topEnd = 30f))
                .background(color = MaterialTheme.colorScheme.secondary)
                .padding(Constants.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            if (selectedPlaylistState.playlistWithMusics != null) {
                items(selectedPlaylistState.playlistWithMusics!!.musics) { music ->
                    MusicItemComposable(
                        music = music,
                        onClick = onEvent,
                        onLongClick = {
                            coroutineScope.launch {
                                modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    )
                }
            }
        }
    }
}