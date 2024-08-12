package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MusicList(
    modifier: Modifier = Modifier,
    musics: List<Music>,
    playlistId: UUID?,
    onShowMusicBottomSheet: (Music) -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    isMainPlaylist: Boolean = false,
    updateNbPlayedAction: (UUID) -> Unit,
    onPrimaryColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

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
                onLongClick = { onShowMusicBottomSheet(music) },
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