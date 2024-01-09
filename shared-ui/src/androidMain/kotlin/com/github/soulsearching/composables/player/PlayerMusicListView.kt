package com.github.soulsearching.composables.player

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.ImageCover
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.bottomsheet.music.MusicBottomSheetEvents
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.draggablestates.PlayerMusicListDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.utils.SettingsUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("UnnecessaryComposedModifier", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerMusicListView(
    coverList: ArrayList<ImageCover>,
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicListDraggableState: PlayerMusicListDraggableState,
    playerDraggableState: PlayerDraggableState,
    playerMusicListViewModel: PlayerMusicListViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val playerListState = rememberLazyListState()

    BackHandler(musicListDraggableState.state.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
        }
    }

    val mainBoxClickableModifier =
        if (musicListDraggableState.state.currentValue == BottomSheetStates.EXPANDED) {
            Modifier.clickable {
                coroutineScope.launch {
                    musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
                }
            }
        } else {
            Modifier
        }

    val primaryColor: Color by animateColorAsState(
        targetValue =
        if (SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlayerThemeOn()) {
            ColorPaletteUtils.getDynamicPrimaryColor(
                baseColor = PlayerUtils.playerViewModel.handler.currentColorPalette?.rgb
            )
        } else {
            MaterialTheme.colorScheme.primary
        },
        tween(Constants.AnimationDuration.normal),
        label = "PRIMARY_COLOR_PLAYER_MUSIC_LIST_VIEW"
    )

    val secondaryColor: Color by animateColorAsState(
        targetValue =
        if (SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlayerThemeOn()
        ) {
            ColorPaletteUtils.getDynamicSecondaryColor(
                baseColor = PlayerUtils.playerViewModel.handler.currentColorPalette?.rgb
            )
        } else {
            MaterialTheme.colorScheme.secondary
        },
        tween(Constants.AnimationDuration.normal),
        label = "SECONDARY_COLOR_PLAYER_MUSIC_LIST_VIEW"
    )

    val textColor: Color by animateColorAsState(
        targetValue =
        if (SettingsUtils.settingsViewModel.handler.isPersonalizedDynamicPlayerThemeOn()
        ) {
            Color.White
        } else {
            MaterialTheme.colorScheme.onPrimary
        },
        tween(Constants.AnimationDuration.normal),
        label = "TEXT_COLOR_COLOR_PLAYER_MUSIC_LIST_VIEW"
    )

    MusicBottomSheetEvents(
        musicBottomSheetState = MusicBottomSheetState.PLAYER,
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = onMusicEvent,
        onPlaylistsEvent = onPlaylistEvent,
        navigateToModifyMusic = { path ->
            coroutineScope.launch {
                musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
            }.invokeOnCompletion {
                coroutineScope.launch {
                    playerDraggableState.animateTo(BottomSheetStates.MINIMISED)
                }.invokeOnCompletion {
                    navigateToModifyMusic(path)
                }
            }
        },
        playerMusicListViewModel = playerMusicListViewModel,
        playerDraggableState = playerDraggableState,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        onSecondaryColor = textColor,
        onPrimaryColor = textColor
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = musicListDraggableState.state.offset.roundToInt()
                )
            }
            .anchoredDraggable(
                state = musicListDraggableState.state,
                orientation = Orientation.Vertical
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = secondaryColor,
                    shape = RoundedCornerShape(
                        topStartPercent = 4,
                        topEndPercent = 4
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Constants.Spacing.medium)
                    .composed {
                        mainBoxClickableModifier
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.size(Constants.ImageSize.medium))
                Text(
                    text = stringResource(id = R.string.played_list),
                    color = textColor,
                    fontSize = 15.sp
                )
                Image(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = "",
                    modifier = Modifier
                        .size(Constants.ImageSize.medium)
                        .clickable {
                            coroutineScope.launch {
                                val currentIndex =
                                    PlayerUtils.playerViewModel.handler.getIndexOfCurrentMusic()
                                if (currentIndex != -1) {
                                    playerListState.animateScrollToItem(
                                        currentIndex
                                    )
                                }
                            }
                        },
                    colorFilter = ColorFilter.tint(textColor)
                )
            }

            LazyColumn(
                state = playerListState
            ) {
                items(
                    items = PlayerUtils.playerViewModel.handler.currentPlaylist,
                ) { elt ->
                    MusicItemComposable(
                        music = elt,
                        onClick = { music ->
                            coroutineScope.launch {
                                musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
                            }.invokeOnCompletion {
                                PlayerUtils.playerViewModel.handler.setCurrentPlaylistAndMusic(
                                    music = music,
                                    playlist = musicState.musics,
                                    playlistId = PlayerUtils.playerViewModel.handler.currentPlaylistId,
                                    isMainPlaylist = PlayerUtils.playerViewModel.handler.isMainPlaylist
                                )
                            }
                        },
                        onLongClick = {
                            coroutineScope.launch {
                                onMusicEvent(
                                    MusicEvent.SetSelectedMusic(
                                        elt
                                    )
                                )
                                onMusicEvent(
                                    MusicEvent.BottomSheet(
                                        isShown = true
                                    )
                                )
                            }
                        },
                        musicCover = coverList.find { it.coverId == elt.coverId }?.cover,
                        textColor = textColor
                    )
                }
            }
        }
    }
}
