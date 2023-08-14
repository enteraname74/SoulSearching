package com.github.soulsearching.composables

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.classes.MusicBottomSheetState
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
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
    swipeableState: SwipeableState<BottomSheetStates>,
    playlistId: UUID?,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    isMainPlaylist: Boolean = false,
    playerSwipeableState: SwipeableState<BottomSheetStates>
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
        playerSwipeableState = playerSwipeableState
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
                        swipeableState.animateTo(BottomSheetStates.EXPANDED)
                    }.invokeOnCompletion {
                        if (!PlayerUtils.playerViewModel.isSamePlaylist(
                                isMainPlaylist = isMainPlaylist,
                                playlistId = playlistId
                            )
                        ) {
                            playerMusicListViewModel.savePlayerMusicList(
                                musicState.musics
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
                musicCover = retrieveCoverMethod(music.coverId)
            )

        }
    }
}