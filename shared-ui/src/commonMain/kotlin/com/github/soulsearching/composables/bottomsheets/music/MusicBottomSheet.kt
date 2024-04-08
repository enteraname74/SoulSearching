package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.mainpage.domain.state.MusicState
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
@Composable
fun MusicBottomSheet(
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    musicModalSheetState: SheetState,
    musicState: MusicState,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement()
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
                onMusicEvent(MusicEvent.UpdateQuickAccessState(
                    musicId = musicState.selectedMusic.musicId
                ))
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
                    playbackManager.removeSongFromPlayedPlaylist(
                        musicId = musicState.selectedMusic.musicId
                    )
                    playerMusicListViewModel.handler.savePlayerMusicList(
                        playbackManager.playedList.map { it.musicId }
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
                        if (playerDraggableState.currentValue == BottomSheetStates.COLLAPSED) {
                            playerDraggableState.animateTo(BottomSheetStates.MINIMISED, tween(
                                Constants.AnimationDuration.normal)
                            )
                        }
                    }.invokeOnCompletion {
                        playbackManager.addMusicToPlayNext(
                            music = musicState.selectedMusic
                        )
                        playerMusicListViewModel.handler.savePlayerMusicList(playbackManager.playedList.map { it.musicId })
                    }
                }
            },
            isInQuickAccess = musicState.selectedMusic.isInQuickAccess,
            isCurrentlyPlaying = playbackManager.isSameMusicAsCurrentPlayedOne(musicState.selectedMusic.musicId)
        )
    }
}