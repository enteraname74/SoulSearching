package com.github.soulsearching.elementpage.playlistpage.presentation.composable

import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.mainpage.domain.state.MusicState
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.UUID

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
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playlistId: UUID?,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    isMainPlaylist: Boolean = false,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    updateNbPlayedAction: (UUID) -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    onPrimaryColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    onSecondaryColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement()
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
                        playerDraggableState.animateTo(BottomSheetStates.EXPANDED, tween(Constants.AnimationDuration.normal))
                    }.invokeOnCompletion {
                        playlistId?.let {
                            updateNbPlayedAction(it)
                        }

                        if (!playbackManager.isSamePlaylist(
                                isMainPlaylist = isMainPlaylist,
                                playlistId = playlistId
                            )
                        ) {
                            playerMusicListViewModel.handler.savePlayerMusicList(
                                musicState.musics.map { it.musicId }
                            )
                        }
                        playbackManager.setCurrentPlaylistAndMusic(
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
                textColor = onPrimaryColor,
                isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(music.musicId)
            )
        }
        item {
            PlayerSpacer()
        }
    }
}