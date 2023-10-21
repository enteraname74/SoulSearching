package com.github.soulsearching.composables.bottomSheets

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.swipeable
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
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.ColorPaletteUtils
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.classes.enumsAndTypes.MusicBottomSheetState
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.SettingsUtils
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("UnnecessaryComposedModifier", "CoroutineCreationDuringComposition")
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
    musicListSwipeableState: SwipeableState<BottomSheetStates>,
    playerSwipeableState: SwipeableState<BottomSheetStates>,
    playerMusicListViewModel: PlayerMusicListViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val playerListState = rememberLazyListState()

    BackHandler(musicListSwipeableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            musicListSwipeableState.animateTo(BottomSheetStates.COLLAPSED, tween(Constants.AnimationTime.long))
        }
    }

    val mainBoxClickableModifier =
        if (musicListSwipeableState.currentValue == BottomSheetStates.EXPANDED) {
            Modifier.clickable {
                coroutineScope.launch {
                    musicListSwipeableState.animateTo(BottomSheetStates.COLLAPSED, tween(Constants.AnimationTime.long))
                }
            }
        } else {
            Modifier
        }

    val primaryColor: Color by animateColorAsState(
        targetValue =
        if (PlayerUtils.playerViewModel.currentColorPalette == null
            || !SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
        ) {
            DynamicColor.primary
        } else {
            ColorPaletteUtils.getDynamicPrimaryColor()
        },
        tween(Constants.AnimationTime.long)
    )

    val secondaryColor: Color by animateColorAsState(
        targetValue =
        if (PlayerUtils.playerViewModel.currentColorPalette == null
            || !SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
        ) {
            DynamicColor.secondary
        } else {
            ColorPaletteUtils.getDynamicSecondaryColor()
        },
        tween(Constants.AnimationTime.long)
    )

    val textColor: Color by animateColorAsState(
        targetValue =
        if (PlayerUtils.playerViewModel.currentColorPalette == null
            || !SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
        ) {
            DynamicColor.onSecondary
        } else {
            Color.White
        },
        tween(Constants.AnimationTime.long)
    )

    MusicBottomSheetEvents(
        musicBottomSheetState = MusicBottomSheetState.PLAYER,
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = onMusicEvent,
        onPlaylistsEvent = onPlaylistEvent,
        navigateToModifyMusic = { path ->
            coroutineScope.launch {
                musicListSwipeableState.animateTo(BottomSheetStates.COLLAPSED, tween(Constants.AnimationTime.long))
            }.invokeOnCompletion {
                coroutineScope.launch {
                    playerSwipeableState.animateTo(BottomSheetStates.MINIMISED, tween(Constants.AnimationTime.long))
                }.invokeOnCompletion {
                    navigateToModifyMusic(path)
                }
            }
        },
        playerMusicListViewModel = playerMusicListViewModel,
        playerSwipeableState = playerSwipeableState,
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
                    y = musicListSwipeableState.offset.value.roundToInt()
                )
            }
            .swipeable(
                state = musicListSwipeableState,
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
                                    PlayerUtils.playerViewModel.getIndexOfCurrentMusic()
                                if (currentIndex != -1) {
                                    playerListState.animateScrollToItem(
                                        currentIndex
                                    )
                                }
                            }
                        },
                    colorFilter = ColorFilter.tint(DynamicColor.onSecondary)
                )
            }

            LazyColumn(
                state = playerListState
            ) {
                items(
                    items = PlayerUtils.playerViewModel.currentPlaylist,
                ) { elt ->
                    MusicItemComposable(
                        music = elt,
                        onClick = { music ->
                            coroutineScope.launch {
                                musicListSwipeableState.animateTo(
                                    BottomSheetStates.COLLAPSED,
                                    tween(Constants.AnimationTime.long)
                                )
                            }.invokeOnCompletion {
                                PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                                    music = music,
                                    playlist = musicState.musics,
                                    playlistId = PlayerUtils.playerViewModel.currentPlaylistId,
                                    isMainPlaylist = PlayerUtils.playerViewModel.isMainPlaylist
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
                        primaryColor = secondaryColor,
                        textColor = textColor
                    )
                }
            }
        }
    }
}
