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
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.soulsearching.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
@Composable
fun MusicBottomSheet(
    musicModalSheetState: SheetState,
    selectedMusic: Music,
    onDismiss: () -> Unit,
    onShowDeleteMusicDialog: () -> Unit,
    onShowRemoveFromPlaylistDialog: () -> Unit,
    onToggleQuickAccessState: () -> Unit,
    showAddToPlaylistBottomSheet: () -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                            onDismiss()
                            navigateToModifyMusic(selectedMusic.musicId.toString())
                        }
                    }
            },
            quickAccessAction = {
                onToggleQuickAccessState()
                coroutineScope.launch { musicModalSheetState.hide() }
                    .invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) onDismiss()
                    }
            },
            removeAction = onShowDeleteMusicDialog,
            addToPlaylistAction = {
//                onPlaylistEvent(
//                    PlaylistEvent.PlaylistsSelection(
//                        musicId = selectedMusic.musicId
//                    )
//                )
//                onMusicEvent(
//                    MusicEvent.AddToPlaylistBottomSheet(
//                        isShown = true
//                    )
//                )
                showAddToPlaylistBottomSheet()
            },
            removeFromPlaylistAction = onShowRemoveFromPlaylistDialog,
            removeFromPlayedListAction = {
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.removeSongFromPlayedPlaylist(
                        musicId = selectedMusic.musicId
                    )
                    coroutineScope.launch {
                        musicModalSheetState.hide()
                    }.invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) onDismiss()
                    }
                }
            },
            playNextAction = {
                CoroutineScope(Dispatchers.IO).launch {
                    coroutineScope.launch {
                        musicModalSheetState.hide()
                    }.invokeOnCompletion {
                        if (!musicModalSheetState.isVisible) onDismiss()
                    }
                }
                coroutineScope.launch {
                    if (playerDraggableState.currentValue == BottomSheetStates.COLLAPSED) {
                        playerDraggableState.animateTo(
                            BottomSheetStates.MINIMISED, tween(
                                UiConstants.AnimationDuration.normal
                            )
                        )
                    }
                }.invokeOnCompletion {
                    playbackManager.addMusicToPlayNext(
                        music = selectedMusic
                    )
                }
            },
            isInQuickAccess = selectedMusic.isInQuickAccess,
            isCurrentlyPlaying = playbackManager.isSameMusicAsCurrentPlayedOne(selectedMusic.musicId)
        )
    }
}