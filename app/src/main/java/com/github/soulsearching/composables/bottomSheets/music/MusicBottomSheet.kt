package com.github.soulsearching.composables.bottomSheets.music

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.classes.MusicBottomSheetState
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MusicBottomSheet(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState: SheetState,
    musicState: MusicState,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerSwipeableState: SwipeableState<BottomSheetStates>
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = {
            onMusicEvent(
                MusicEvent.BottomSheet(
                    isShown = false
                )
            )
        },
        sheetState = musicModalSheetState,
        dragHandle = {}
    ) {
        MusicBottomSheetMenu(
            musicBottomSheetState = musicBottomSheetState,
            modifyAction = {
                coroutineScope.launch { musicModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) {
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                            navigateToModifyMusic(musicState.selectedMusic.musicId.toString())
                        }
                    }
            },
            quickAccessAction = {
                onMusicEvent(MusicEvent.UpdateQuickAccessState)
                coroutineScope.launch { musicModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) {
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
            },
            removeAction = {
                onMusicEvent(MusicEvent.DeleteDialog(isShown = true))
            },
            addToPlaylistAction = {
                onPlaylistEvent(
                    PlaylistEvent.PlaylistsSelection(
                        musicId = musicState.selectedMusic.musicId
                    )
                )
                onMusicEvent(
                    MusicEvent.AddToPlaylistBottomSheet(
                        isShown = true
                    )
                )
            },
            removeFromPlaylistAction = {
                onMusicEvent(MusicEvent.RemoveFromPlaylistDialog(isShown = true))
            },
            removeFromPlayedListAction = {
                CoroutineScope(Dispatchers.IO).launch {
                    PlayerUtils.playerViewModel.removeMusicFromCurrentPlaylist(
                        musicId = musicState.selectedMusic.musicId,
                        context = context
                    )
                    playerMusicListViewModel.savePlayerMusicList(
                        PlayerUtils.playerViewModel.currentPlaylist
                    )

                    coroutineScope.launch {
                        musicModalSheetState.hide()
                    }.invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) {
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
                }
            },
            playNextAction = {
                CoroutineScope(Dispatchers.IO).launch {
                    coroutineScope.launch {
                        musicModalSheetState.hide()
                    }.invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) {
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = false
                                )
                            )
                        }
                    }
                    coroutineScope.launch {
                        if (playerSwipeableState.currentValue == BottomSheetStates.COLLAPSED) {
                            playerSwipeableState.animateTo(BottomSheetStates.MINIMISED, tween(
                                Constants.AnimationTime.normal)
                            )
                        }
                    }.invokeOnCompletion {
                        PlayerUtils.playerViewModel.addMusicToPlayNext(
                            music = musicState.selectedMusic,
                            context = context
                        )
                        playerMusicListViewModel.savePlayerMusicList(PlayerUtils.playerViewModel.currentPlaylist)
                    }
                }
            },
            isInQuickAccess = musicState.selectedMusic.isInQuickAccess
        )
    }
}