package com.github.soulsearching.composables.bottomsheets.music

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.composables.dialog.SoulSearchingDialog
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.mainpage.domain.state.MusicState
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.strings.strings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
fun MusicBottomSheetEvents(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    onPrimaryColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    onSecondaryColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement()
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
                        playbackManager.playedList.map { it.musicId }
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
                        playbackManager.playedList.map { it.musicId }
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