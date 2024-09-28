package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowHeightDp
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistVIewUiUtils
import kotlinx.coroutines.launch

@Composable
fun PlaylistRowView(
    navigateBack: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    optionalContent: @Composable () -> Unit = {},
) {
    val windowSize = rememberWindowSize()
    if (windowSize == WindowSize.Large) {
        LargeView(
            navigateBack = navigateBack,
            shuffleAction = shuffleAction,
            searchAction = searchAction,
            onShowMusicBottomSheet = onShowMusicBottomSheet,
            playlistDetail = playlistDetail,
            playlistDetailListener = playlistDetailListener,
            optionalContent = optionalContent,
        )
    } else {
        MediumView(
            navigateBack = navigateBack,
            shuffleAction = shuffleAction,
            searchAction = searchAction,
            onShowMusicBottomSheet = onShowMusicBottomSheet,
            playlistDetail = playlistDetail,
            playlistDetailListener = playlistDetailListener,
            optionalContent = optionalContent,
        )
    }
}

@Composable
private fun LargeView(
    navigateBack: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    optionalContent: @Composable () -> Unit = {},
) {

    val windowHeight = rememberWindowHeightDp()
    val windowWidth = rememberWindowWidthDp()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SoulTopBar(leftAction = TopBarNavigationAction(onClick = navigateBack))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Content(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = windowWidth * 0.1f)
                    .padding(top = windowHeight * 0.1f),
                shuffleAction = shuffleAction,
                searchAction = searchAction,
                onShowMusicBottomSheet = onShowMusicBottomSheet,
                playlistDetail = playlistDetail,
                playlistDetailListener = playlistDetailListener,
                optionalContent = optionalContent,
            )
        }
    }
}

@Composable
private fun MediumView(
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    navigateBack: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    optionalContent: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SoulTopBar(leftAction = TopBarNavigationAction(onClick = navigateBack))
        Content(
            modifier = Modifier.fillMaxSize(),
            shuffleAction = shuffleAction,
            searchAction = searchAction,
            onShowMusicBottomSheet = onShowMusicBottomSheet,
            playlistDetail = playlistDetail,
            playlistDetailListener = playlistDetailListener,
            optionalContent = optionalContent,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    modifier: Modifier = Modifier,
    optionalContent: @Composable () -> Unit = {},
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val canShowVerticalInformation = PlaylistVIewUiUtils.canShowVerticalMainInformation()

    Row(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            PageHeader(
                playlistDetail = playlistDetail,
                onSubTitleClicked = playlistDetailListener::onSubtitleClicked
            )
            if (canShowVerticalInformation) {
                PlaylistPanel(
                    editAction = playlistDetailListener.onEdit,
                    shuffleAction = shuffleAction,
                    searchAction = searchAction,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (!canShowVerticalInformation) {
                PlaylistPanel(
                    editAction = playlistDetailListener.onEdit,
                    shuffleAction = shuffleAction,
                    searchAction = searchAction,
                )
            }
            LazyColumn {
                if (canShowVerticalInformation) {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = SoulSearchingColorTheme.colorScheme.primary
                                )
                        ) {
                            optionalContent()
                        }
                    }
                } else {
                    item {
                        optionalContent()
                    }
                }

                items(
                    items = playlistDetail.musics,
                    key = { it.musicId },
                    contentType = { PLAYLIST_MUSICS_CONTENT_TYPE },
                ) { music ->
                    MusicItemComposable(
                        modifier = Modifier
                            .animateItemPlacement(),
                        music = music,
                        onClick = {
                            coroutineScope.launch {
                                playerViewManager.animateTo(newState = BottomSheetStates.EXPANDED)
                            }.invokeOnCompletion {
                                playlistDetailListener.onUpdateNbPlayed()
                                playbackManager.setCurrentPlaylistAndMusic(
                                    music = music,
                                    musicList = playlistDetail.musics,
                                    playlistId = playlistDetail.id,
                                    isMainPlaylist = false,
                                )
                            }
                        },
                        onLongClick = { onShowMusicBottomSheet(music) },
                        isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(music.musicId)
                    )
                }
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}

private const val PLAYLIST_MUSICS_CONTENT_TYPE: String = "PLAYLIST_MUSICS_CONTENT_TYPE"