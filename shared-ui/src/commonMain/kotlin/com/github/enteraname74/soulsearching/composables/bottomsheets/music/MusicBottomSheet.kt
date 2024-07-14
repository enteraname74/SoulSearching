package com.github.enteraname74.soulsearching.composables.bottomsheets.music

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class,)
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
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
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
                    if (playerViewManager.currentValue == BottomSheetStates.COLLAPSED) {
                        playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
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