package com.github.soulsearching.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.utils.PlayerUtils
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
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
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
        musicState = musicState,
        playlistState = playlistState,
        onMusicEvent = onMusicEvent,
        onPlaylistsEvent = onPlaylistEvent,
        navigateToModifyMusic = navigateToModifyMusic,
        musicBottomSheetState = musicBottomSheetState,
        playerMusicListViewModel = playerMusicListViewModel,
        playerDraggableState = playerDraggableState,
        secondaryColor = secondaryColor,
        onSecondaryColor = onSecondaryColor,
        primaryColor = primaryColor,
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

                        if (!PlayerUtils.playerViewModel.handler.isSamePlaylist(
                                isMainPlaylist = isMainPlaylist,
                                playlistId = playlistId
                            )
                        ) {
                            playerMusicListViewModel.handler.savePlayerMusicList(
                                musicState.musics.map { it.musicId } as ArrayList<UUID>
                            )
                        }
                        PlayerUtils.playerViewModel.handler.setCurrentPlaylistAndMusic(
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