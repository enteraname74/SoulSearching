package com.github.soulsearching.composables


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.classes.MusicBottomSheetState
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.composables.playlistComposable.ColumnPlaylistPanel
import com.github.soulsearching.composables.playlistComposable.RowPlaylistPanel
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
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
    val orientation = LocalConfiguration.current.orientation
    val coroutineScope = rememberCoroutineScope()

    val shuffleAction = {
        coroutineScope
            .launch {
                swipeableState.animateTo(BottomSheetStates.EXPANDED)
            }
            .invokeOnCompletion {
                PlayerUtils.playerViewModel.playShuffle(musicState.musics)
                playerMusicListViewModel.savePlayerMusicList(PlayerUtils.playerViewModel.currentPlaylist)
            }
    }

    when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {

        }
        else -> {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(DynamicColor.primary)
            ) {
                PlaylistHeaderBar(
                    title = title,
                    navigateBack = navigateBack
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DynamicColor.primary)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Constants.Spacing.large),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppImage(
                                bitmap = image,
                                size = Constants.ImageSize.huge,
                                roundedPercent = 5
                            )
                        }
                    }
                    stickyHeader {
                        RowPlaylistPanel(
                            editAction = navigateToModifyPlaylist,
                            shuffleAction = { shuffleAction() }
                        )
                    }
                    items(items = musicState.musics, key = { music -> music.musicId }) { music ->
                        Row(Modifier.animateItemPlacement()) {
                            MusicItemComposable(
                                music = music,
                                onClick = { music ->
                                    coroutineScope.launch {
                                        swipeableState.animateTo(BottomSheetStates.EXPANDED)
                                    }.invokeOnCompletion {
                                        PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                                            music = music,
                                            playlist = musicState.musics,
                                            isMainPlaylist = true,
                                            playlistId = null,
                                            bitmap = retrieveCoverMethod(music.coverId)
                                        )
                                        playerMusicListViewModel.savePlayerMusicList(PlayerUtils.playerViewModel.currentPlaylist)
                                    }
                                },
                                onLongClick = {
                                    coroutineScope.launch {
                                        onMusicEvent(
                                            MusicEvent.SetSelectedMusic(
                                                music
                                            )
                                        )
                                        onMusicEvent(
                                            MusicEvent.BottomSheet(
                                                isShown = true
                                            )
                                        )
                                    }
                                },
                                musicCover = retrieveCoverMethod(music.coverId),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistHeaderBar(
    title: String,
    navigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DynamicColor.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.back_button),
                tint = DynamicColor.onPrimary
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                maxLines = 2,
                fontSize = 18.sp,
                color = DynamicColor.onPrimary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
        }

        Spacer(modifier = Modifier.size(48.dp))
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