package com.github.soulsearching.composables.bottomsheet.music

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.types.MusicBottomSheetState
import com.github.soulsearching.composables.dialog.SoulSearchingDialog
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MusicBottomSheetEvents(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistsEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    playerMusicListViewModel: PlayerMusicListViewModel,
    playerDraggableState: PlayerDraggableState,
    primaryColor: Color = DynamicColor.primary,
    secondaryColor: Color = DynamicColor.secondary,
    onPrimaryColor: Color = DynamicColor.onPrimary,
    onSecondaryColor: Color = DynamicColor.onSecondary
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val musicModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val addToPlaylistModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    BackHandler(addToPlaylistModalSheetState.isVisible) {
        coroutineScope.launch { addToPlaylistModalSheetState.hide() }
    }

    BackHandler(musicModalSheetState.isVisible) {
        coroutineScope.launch { musicModalSheetState.hide() }
    }

    if (musicState.isDeleteDialogShown) {
        SoulSearchingDialog(
            title = stringResource(id = R.string.delete_music_dialog_title),
            text = stringResource(id = R.string.delete_music_dialog_text),
            confirmAction = {
                onMusicEvent(MusicEvent.DeleteMusic)
                onMusicEvent(MusicEvent.DeleteDialog(isShown = false))
                CoroutineScope(Dispatchers.IO).launch {
                    PlayerUtils.playerViewModel.removeMusicFromCurrentPlaylist(
                        musicId = musicState.selectedMusic.musicId,
                        context = context
                    )
                    playerMusicListViewModel.savePlayerMusicList(
                        PlayerUtils.playerViewModel.currentPlaylist.map { it.musicId } as ArrayList<UUID>
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
            primaryColor = primaryColor,
            textColor = onPrimaryColor
        )
    }

    if (musicState.isRemoveFromPlaylistDialogShown) {
        SoulSearchingDialog(
            title = stringResource(id = R.string.remove_music_from_playlist_title),
            text = stringResource(id = R.string.remove_music_from_playlist_text),
            confirmAction = {
                onPlaylistsEvent(PlaylistEvent.RemoveMusicFromPlaylist(musicId = musicState.selectedMusic.musicId))
                onMusicEvent(MusicEvent.RemoveFromPlaylistDialog(isShown = false))
                CoroutineScope(Dispatchers.IO).launch {
                    PlayerUtils.playerViewModel.removeMusicIfSamePlaylist(
                        musicId = musicState.selectedMusic.musicId,
                        context = context,
                        playlistId = playlistState.selectedPlaylist.playlistId
                    )
                    playerMusicListViewModel.savePlayerMusicList(
                        PlayerUtils.playerViewModel.currentPlaylist.map { it.musicId } as ArrayList<UUID>
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
            primaryColor = primaryColor,
            textColor = onPrimaryColor
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