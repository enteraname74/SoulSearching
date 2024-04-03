package com.github.soulsearching.composables.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.swipeable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.ImageCover
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerMusicListView(
    maxHeight: Float,
    coverList: ArrayList<ImageCover>,
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicListDraggableState: SwipeableState<BottomSheetStates>,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    playerMusicListViewModel: PlayerMusicListViewModel,
    colorThemeManager: ColorThemeManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()
    val playerListState = rememberLazyListState()

    SoulSearchingBackHandler(musicListDraggableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            musicListDraggableState.animateTo(
                BottomSheetStates.COLLAPSED,
                tween(Constants.AnimationDuration.normal)
            )
        }
    }

    val mainBoxClickableModifier =
        if (musicListDraggableState.currentValue == BottomSheetStates.EXPANDED) {
            Modifier.clickable {
                coroutineScope.launch {
                    musicListDraggableState.animateTo(
                        BottomSheetStates.COLLAPSED,
                        tween(Constants.AnimationDuration.normal)
                    )
                }
            }
        } else {
            Modifier
        }

    val primaryColor: Color by animateColorAsState(
        targetValue =
        if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
            ColorPaletteUtils.getDynamicPrimaryColor(
                baseColor = colorThemeManager.currentColorPalette?.rgb
            )
        } else {
            SoulSearchingColorTheme.defaultTheme.primary
        },
        tween(Constants.AnimationDuration.normal),
        label = "PRIMARY_COLOR_PLAYER_MUSIC_LIST_VIEW"
    )

    val secondaryColor: Color by animateColorAsState(
        targetValue =
        if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()
        ) {
            ColorPaletteUtils.getDynamicSecondaryColor(
                baseColor = colorThemeManager.currentColorPalette?.rgb
            )
        } else {
            SoulSearchingColorTheme.defaultTheme.secondary
        },
        tween(Constants.AnimationDuration.normal),
        label = "SECONDARY_COLOR_PLAYER_MUSIC_LIST_VIEW"
    )

    val textColor: Color by animateColorAsState(
        targetValue =
        if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()
        ) {
            Color.White
        } else {
            SoulSearchingColorTheme.defaultTheme.onPrimary
        },
        tween(Constants.AnimationDuration.normal),
        label = "TEXT_COLOR_COLOR_PLAYER_MUSIC_LIST_VIEW"
    )

    MusicBottomSheetEvents(
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = onMusicEvent,
        onPlaylistsEvent = onPlaylistEvent,
        navigateToModifyMusic = { path ->
            coroutineScope.launch {
                musicListDraggableState.animateTo(
                    BottomSheetStates.COLLAPSED,
                    tween(Constants.AnimationDuration.normal)
                )
            }.invokeOnCompletion {
                coroutineScope.launch {
                    playerDraggableState.animateTo(
                        BottomSheetStates.MINIMISED,
                        tween(Constants.AnimationDuration.normal)
                    )
                }.invokeOnCompletion {
                    navigateToModifyMusic(path)
                }
            }
        },
        musicBottomSheetState = MusicBottomSheetState.PLAYER,
        playerMusicListViewModel = playerMusicListViewModel,
        playerDraggableState = playerDraggableState,
        primaryColor = primaryColor,
        onPrimaryColor = textColor,
        secondaryColor = secondaryColor,
        onSecondaryColor = textColor
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = musicListDraggableState.offset.value.roundToInt()
                )
            }
            .swipeable(
                state = musicListDraggableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    0f to BottomSheetStates.EXPANDED,
                    maxHeight to BottomSheetStates.COLLAPSED,
                )
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
                    text = strings.playedList,
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
                                musicListDraggableState.animateTo(
                                    BottomSheetStates.COLLAPSED,
                                    tween(Constants.AnimationDuration.normal)
                                )
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
