package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistColumnView(
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    navigateBack: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    optionalContent: @Composable () -> Unit = {},
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SoulTopBar(
            leftAction = TopBarNavigationAction(onClick = navigateBack)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                PageHeader(
                    playlistDetail = playlistDetail,
                    onSubTitleClicked = playlistDetailListener::onSubtitleClicked,
                    onCoverLoaded = onCoverLoaded,
                )
            }
            stickyHeader {
                PlaylistPanel(
                    editAction = playlistDetailListener.onEdit,
                    shuffleAction = {
                        playlistDetailListener.onUpdateNbPlayed()
                        shuffleAction()
                    },
                    searchAction = { searchAction() },
                )
            }
            item {
                optionalContent()
            }
            items(
                items = playlistDetail.musics,
                key = { it.musicId },
                contentType = { PLAYLIST_MUSIC_CONTENT_TYPE }
            ) { elt ->
                MusicItemComposable(
                    music = elt,
                    onClick = { music ->
                        playlistDetailListener.onUpdateNbPlayed()
                        coroutineScope.launch {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = music,
                                musicList = playlistDetail.musics,
                                playlistId = playlistDetail.id,
                                isMainPlaylist = false
                            )
                            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                        }
                    },
                    onLongClick = { onShowMusicBottomSheet(elt) },
                    textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    isPlayedMusic = currentPlayedSong?.musicId == elt.musicId
                )
            }
            item { SoulPlayerSpacer() }
        }
    }
}

private const val PLAYLIST_MUSIC_CONTENT_TYPE: String = "PLAYLIST_MUSIC_CONTENT_TYPE"