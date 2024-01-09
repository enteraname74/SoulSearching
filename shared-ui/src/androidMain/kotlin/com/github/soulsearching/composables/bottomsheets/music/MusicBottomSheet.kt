package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
actual fun MusicBottomSheet(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState: SheetState,
    musicState: MusicState,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: PlayerDraggableState,
    primaryColor: Color,
    textColor: Color
) {
    val coroutineScope = rememberCoroutineScope()

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
            primaryColor = primaryColor,
            textColor = textColor,
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
                    PlayerUtils.playerViewModel.handler.removeMusicFromCurrentPlaylist(
                        musicId = musicState.selectedMusic.musicId
                    )
                    playerMusicListViewModel.handler.savePlayerMusicList(
                        PlayerUtils.playerViewModel.handler.currentPlaylist.map { it.musicId } as ArrayList<UUID>
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
                        if (playerDraggableState.state.currentValue == BottomSheetStates.COLLAPSED) {
                            playerDraggableState.animateTo(BottomSheetStates.MINIMISED)
                        }
                    }.invokeOnCompletion {
                        PlayerUtils.playerViewModel.handler.addMusicToPlayNext(
                            music = musicState.selectedMusic
                        )
                        playerMusicListViewModel.handler.savePlayerMusicList(PlayerUtils.playerViewModel.handler.currentPlaylist.map { it.musicId } as ArrayList<UUID>)
                    }
                }
            },
            isInQuickAccess = musicState.selectedMusic.isInQuickAccess,
            isCurrentlyPlaying = PlayerUtils.playerViewModel.handler.isSameMusic(musicState.selectedMusic.musicId)
        )
    }
}