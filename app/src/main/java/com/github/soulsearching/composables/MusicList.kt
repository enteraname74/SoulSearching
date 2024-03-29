package com.github.soulsearching.composables

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.types.BottomSheetStates
import com.github.soulsearching.classes.types.MusicBottomSheetState
import com.github.soulsearching.composables.bottomsheet.music.MusicBottomSheetEvents
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MusicList(
    musicState: MusicState,
    playlistState: PlaylistState,
    onMusicEvent: (MusicEvent) -> Unit,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
    navigateToModifyMusic: (String) -> Unit,
    modifier: Modifier = Modifier,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    playlistId: UUID?,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    isMainPlaylist: Boolean = false,
    playerDraggableState: PlayerDraggableState,
    updateNbPlayedAction: (UUID) -> Unit,
    primaryColor: Color = DynamicColor.primary,
    secondaryColor: Color = DynamicColor.secondary,
    onPrimaryColor: Color = DynamicColor.onPrimary,
    onSecondaryColor: Color = DynamicColor.onSecondary
) {
    val coroutineScope = rememberCoroutineScope()

    MusicBottomSheetEvents(
        musicBottomSheetState = musicBottomSheetState,
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = onMusicEvent,
        onPlaylistsEvent = onPlaylistEvent,
        navigateToModifyMusic = navigateToModifyMusic,
        playerMusicListViewModel = playerMusicListViewModel,
        playerDraggableState = playerDraggableState,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        onSecondaryColor = onSecondaryColor,
        onPrimaryColor = onPrimaryColor
    )

    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = musicState.musics
        ) { music ->
            MusicItemComposable(
                music = music,
                onClick = {
                    coroutineScope.launch {
                        playerDraggableState.animateTo(BottomSheetStates.EXPANDED)
                    }.invokeOnCompletion {
                        playlistId?.let {
                            updateNbPlayedAction(it)
                        }

                        if (!PlayerUtils.playerViewModel.isSamePlaylist(
                                isMainPlaylist = isMainPlaylist,
                                playlistId = playlistId
                            )
                        ) {
                            playerMusicListViewModel.savePlayerMusicList(
                                musicState.musics.map { it.musicId } as ArrayList<UUID>
                            )
                        }
                        PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                            music = music,
                            playlist = musicState.musics,
                            playlistId = playlistId,
                            isMainPlaylist = isMainPlaylist
                        )
                    }
                },
                onLongClick = {
                    coroutineScope.launch {
                        onMusicEvent(MusicEvent.SetSelectedMusic(music))
                        onMusicEvent(MusicEvent.BottomSheet(isShown = true))
                    }
                },
                musicCover = retrieveCoverMethod(music.coverId),
                textColor = onPrimaryColor
            )
        }
        item {
            PlayerSpacer()
        }
    }
}