package com.github.soulsearching.composables

import android.graphics.Bitmap
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.classes.enumsAndTypes.MusicBottomSheetState
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
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
    playerSwipeableState: SwipeableState<BottomSheetStates>,
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
        playerSwipeableState = playerSwipeableState,
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
                primaryColor = primaryColor,
                textColor = onPrimaryColor,
                music = music,
                onClick = {
                    coroutineScope.launch {
                        swipeableState.animateTo(BottomSheetStates.EXPANDED)
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
                musicCover = retrieveCoverMethod(music.coverId)
            )
        }
        item {
            PlayerSpacer()
        }
    }
}