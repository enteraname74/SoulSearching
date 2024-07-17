package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MusicList(
    modifier: Modifier = Modifier,
    selectedMusic: Music?,
    onSelectMusic: (Music) -> Unit,
    musics: List<Music>,
    playlistsWithMusics: List<PlaylistWithMusics>,
    playlistId: UUID?,
    isDeleteMusicDialogShown: Boolean,
    isBottomSheetShown: Boolean,
    isAddToPlaylistBottomSheetShown: Boolean,
    isRemoveFromPlaylistDialogShown: Boolean = false,
    onSetBottomSheetVisibility: (Boolean) -> Unit,
    onSetDeleteMusicDialogVisibility: (Boolean) -> Unit,
    onSetRemoveMusicFromPlaylistDialogVisibility: (Boolean) -> Unit = {},
    onSetAddToPlaylistBottomSheetVisibility: (Boolean) -> Unit = {},
    onDeleteMusic: (Music) -> Unit,
    onToggleQuickAccessState: (Music) -> Unit,
    onRemoveFromPlaylist: (Music) -> Unit = {},
    onAddMusicToSelectedPlaylists: (selectedPlaylistsIds: List<UUID>, selectedMusic: Music) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    isMainPlaylist: Boolean = false,
    updateNbPlayedAction: (UUID) -> Unit,
    musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    onPrimaryColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    onSecondaryColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    selectedMusic?.let { music ->
        MusicBottomSheetEvents(
            selectedMusic = music,
            currentPlaylistId = playlistId,
            playlistsWithMusics = playlistsWithMusics,
            isAddToPlaylistBottomSheetShown = isAddToPlaylistBottomSheetShown,
            isRemoveFromPlaylistDialogShown = isRemoveFromPlaylistDialogShown,
            onDismiss = { onSetBottomSheetVisibility(false) },
            onSetRemoveMusicFromPlaylistDialogVisibility = onSetRemoveMusicFromPlaylistDialogVisibility,
            onSetAddToPlaylistBottomSheetVisibility = onSetAddToPlaylistBottomSheetVisibility,
            onRemoveFromPlaylist = { onRemoveFromPlaylist(music) },
            onAddMusicToSelectedPlaylists = { selectedPlaylistsIds ->
                onAddMusicToSelectedPlaylists(selectedPlaylistsIds, music)
            },
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            onPrimaryColor = onPrimaryColor,
            onSecondaryColor = onSecondaryColor
        )
    }


    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = musics
        ) { music ->
            MusicItemComposable(
                music = music,
                onClick = {
                    coroutineScope.launch {
                        playerViewManager.animateTo(newState = BottomSheetStates.EXPANDED)
                    }.invokeOnCompletion {
                        playlistId?.let {
                            updateNbPlayedAction(it)
                        }

                        playbackManager.setCurrentPlaylistAndMusic(
                            music = music,
                            musicList = musics,
                            playlistId = playlistId,
                            isMainPlaylist = isMainPlaylist
                        )
                    }
                },
                onLongClick = {
                    coroutineScope.launch {
                        onSelectMusic(music)
                        onSetBottomSheetVisibility(true)
                    }
                },
                musicCover = retrieveCoverMethod(music.coverId),
                textColor = onPrimaryColor,
                isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(music.musicId)
            )
        }
        item {
            SoulPlayerSpacer()
        }
    }
}