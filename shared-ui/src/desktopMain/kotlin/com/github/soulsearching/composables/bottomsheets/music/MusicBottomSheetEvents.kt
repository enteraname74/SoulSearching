package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.composables.dialog.SoulSearchingDialog
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.strings.strings
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
actual fun MusicBottomSheetEvents(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    primaryColor: Color,
    secondaryColor: Color,
    onPrimaryColor: Color,
    onSecondaryColor: Color,
    playbackManager: PlaybackManager
) {
    val coroutineScope = rememberCoroutineScope()

    val musicModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val addToPlaylistModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    SoulSearchingBackHandler(addToPlaylistModalSheetState.isVisible) {
        coroutineScope.launch { addToPlaylistModalSheetState.hide() }
    }

    SoulSearchingBackHandler(musicModalSheetState.isVisible) {
        coroutineScope.launch { musicModalSheetState.hide() }
    }

    if (musicState.isDeleteDialogShown) {
        SoulSearchingDialog(
            title = strings.deleteMusicDialogTitle,
            text = strings.deleteMusicDialogText,
            confirmAction = {
                onMusicEvent(MusicEvent.DeleteMusic)
                onMusicEvent(MusicEvent.DeleteDialog(isShown = false))
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.removeSongFromPlayedPlaylist(
                        musicId = musicState.selectedMusic.musicId
                    )
                    playerMusicListViewModel.handler.savePlayerMusicList(
                        playbackManager.playedList.map { it.musicId } as ArrayList<UUID>
                    )
                }
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
            dismissAction = {
                onMusicEvent(MusicEvent.DeleteDialog(isShown = false))
            },
            confirmText = strings.delete,
            dismissText = strings.cancel,
            backgroundColor = primaryColor,
            contentColor = onPrimaryColor
        )
    }

    if (musicState.isRemoveFromPlaylistDialogShown) {
        SoulSearchingDialog(
            title = strings.removeMusicFromPlaylistTitle,
            text = strings.removeMusicFromPlaylistText,
            confirmAction = {
                onPlaylistsEvent(PlaylistEvent.RemoveMusicFromPlaylist(musicId = musicState.selectedMusic.musicId))
                onMusicEvent(MusicEvent.RemoveFromPlaylistDialog(isShown = false))
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.removeMusicIfSamePlaylist(
                        musicId = musicState.selectedMusic.musicId,
                        playlistId = playlistState.selectedPlaylist.playlistId
                    )
                    playerMusicListViewModel.handler.savePlayerMusicList(
                        playbackManager.playedList.map { it.musicId } as ArrayList<UUID>
                    )
                }

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
            dismissAction = {
                onMusicEvent(MusicEvent.RemoveFromPlaylistDialog(isShown = false))
            },
            confirmText = strings.delete,
            dismissText = strings.cancel,
            backgroundColor = primaryColor,
            contentColor = onPrimaryColor
        )
    }

    if (musicState.isBottomSheetShown) {
        MusicBottomSheet(
            musicBottomSheetState = musicBottomSheetState,
            onMusicEvent = onMusicEvent,
            onPlaylistEvent = onPlaylistsEvent,
            musicModalSheetState = musicModalSheetState,
            musicState = musicState,
            navigateToModifyMusic = navigateToModifyMusic,
            playerMusicListViewModel = playerMusicListViewModel,
            playerDraggableState = playerDraggableState,
            primaryColor = secondaryColor,
            textColor = onSecondaryColor
        )
    }

    if (musicState.isAddToPlaylistBottomSheetShown) {
        AddToPlaylistBottomSheet(
            selectedMusicId = musicState.selectedMusic.musicId,
            onMusicEvent = onMusicEvent,
            onPlaylistsEvent = onPlaylistsEvent,
            addToPlaylistModalSheetState = addToPlaylistModalSheetState,
            playlistState = playlistState,
            primaryColor = secondaryColor,
            textColor = onSecondaryColor
        )
    }
}