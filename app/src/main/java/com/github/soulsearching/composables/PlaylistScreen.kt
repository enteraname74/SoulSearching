package com.github.soulsearching.composables


import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.classes.MusicBottomSheetState
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.playlistComposable.PlaylistPanel
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.*

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
    val orientation = LocalConfiguration.current.orientation
    val coroutineScope = rememberCoroutineScope()

    MusicBottomSheetEvents(
        musicBottomSheetState = MusicBottomSheetState.PLAYLIST,
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = onMusicEvent,
        onPlaylistsEvent = onPlaylistEvent,
        navigateToModifyMusic = navigateToModifyMusic
    )

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
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    AppHeaderBar(
                        title = title,
                        leftAction = navigateBack
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
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
                PlaylistPanel(
                    editAction = navigateToModifyPlaylist,
                    shuffleAction = { shuffleAction() },
                    isLandscapeMode = true
                )
                MusicList(
                    musicBottomSheetState = MusicBottomSheetState.PLAYLIST,
                    musicState = musicState,
                    playlistState = playlistState,
                    onMusicEvent = onMusicEvent,
                    onPlaylistEvent = onPlaylistEvent,
                    navigateToModifyMusic = navigateToModifyMusic,
                    retrieveCoverMethod = { retrieveCoverMethod(it) },
                    swipeableState = swipeableState,
                    playlistId = playlistId,
                    playerMusicListViewModel = playerMusicListViewModel,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DynamicColor.primary)
            ) {
                AppHeaderBar(
                    title = title,
                    leftAction = navigateBack
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
                        PlaylistPanel(
                            editAction = navigateToModifyPlaylist,
                            shuffleAction = { shuffleAction() },
                            isLandscapeMode = false
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
                                            playlistId = playlistId,
                                            bitmap = retrieveCoverMethod(music.coverId),
                                            isMainPlaylist = false
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