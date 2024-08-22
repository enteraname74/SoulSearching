package com.github.enteraname74.soulsearching.feature.elementpage.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
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
    playerViewManager: PlayerViewManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    optionalContent: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SoulTopBar(leftAction = navigateBack)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                PageHeader(
                    playlistDetail = playlistDetail,
                    onSubTitleClicked = playlistDetailListener::onSubtitleClicked,
                )
            }
            stickyHeader {
                PlaylistPanel(
                    editAction = playlistDetailListener::onEdit,
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
                items = playlistDetail.musics
            ) { elt ->
                MusicItemComposable(
                    music = elt,
                    onClick = { music ->
                        coroutineScope.launch {
                            playerViewManager.animateTo(
                                newState = BottomSheetStates.EXPANDED,
                            )
                        }.invokeOnCompletion {
                            playlistDetailListener.onUpdateNbPlayed()
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = music,
                                musicList = playlistDetail.musics,
                                playlistId = playlistDetail.id,
                                isMainPlaylist = false
                            )
                        }
                    },
                    onLongClick = { onShowMusicBottomSheet(elt) },
                    textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(elt.musicId)
                )
            }
            item { SoulPlayerSpacer() }
        }
    }
}