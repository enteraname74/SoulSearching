package com.github.soulsearching.composables.bottomsheet.music

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.types.BottomSheetStates
import com.github.soulsearching.classes.types.MusicBottomSheetState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun MusicBottomSheet(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState: SheetState,
    musicState: MusicState,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerMusicListViewModel: PlayerMusicListViewModelImpl,
    playerDraggableState: PlayerDraggableState,
    primaryColor: Color = DynamicColor.secondary,
    textColor: Color = DynamicColor.onSecondary
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
                    PlayerUtils.playerViewModel.removeMusicFromCurrentPlaylist(
                        musicId = musicState.selectedMusic.musicId
                    )
                    playerMusicListViewModel.savePlayerMusicList(
                        PlayerUtils.playerViewModel.currentPlaylist.map { it.musicId } as ArrayList<UUID>
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
                        PlayerUtils.playerViewModel.addMusicToPlayNext(
                            music = musicState.selectedMusic
                        )
                        playerMusicListViewModel.savePlayerMusicList(PlayerUtils.playerViewModel.currentPlaylist.map { it.musicId } as ArrayList<UUID>)
                    }
                }
            },
            isInQuickAccess = musicState.selectedMusic.isInQuickAccess,
            isCurrentlyPlaying = PlayerUtils.playerViewModel.isSameMusic(musicState.selectedMusic.musicId)
        )
    }
}