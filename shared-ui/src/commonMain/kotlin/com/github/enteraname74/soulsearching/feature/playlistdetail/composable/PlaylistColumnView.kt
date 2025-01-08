package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.blurCompat
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidth
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch
import kotlin.math.max

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
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    optionalContent: @Composable () -> Unit = {},
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    var topBarHeight: Int by rememberSaveable { mutableStateOf(0) }
    var pageHeaderSize: Int by rememberSaveable { mutableStateOf(0) }

    val backgroundAlpha: Float by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex >= 1) {
                1f
            } else if (pageHeaderSize == 0) {
                0f
            } else {
                (lazyListState.firstVisibleItemScrollOffset.toFloat() / pageHeaderSize)
                    .coerceIn(
                        minimumValue = 0f,
                        maximumValue = 1f,
                    )
            }
        }
    }

    val topBarHeightDp = topBarHeight.toDp()

    val panelTopPadding: Dp =
        if (lazyListState.firstVisibleItemIndex >= 1) {
            topBarHeightDp
        } else if (topBarHeight == 0) {
            0.dp
        } else {
            topBarHeightDp
                .times(lazyListState.firstVisibleItemScrollOffset.toFloat() / pageHeaderSize)
                .coerceIn(0.dp, topBarHeightDp)
        }

    val backgroundColor: Color = if (lazyListState.firstVisibleItemIndex >= 1) {
        SoulSearchingColorTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    BlurredBackground(
        cover = playlistDetail.cover,
        backgroundAlpha = backgroundAlpha,
    ) {
        SoulTopBar(
            modifier = Modifier
                .zIndex(1f)
                .onGloballyPositioned { layoutCoordinates ->
                    topBarHeight = layoutCoordinates.size.height
                },
            leftAction = TopBarNavigationAction(onClick = navigateBack),
            colors = SoulTopBarDefaults.primary(
                containerColor = backgroundColor,
            )
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyListState,
        ) {
            item {
                PageHeader(
                    modifier = Modifier
                        .padding(top = topBarHeightDp)
                        .onGloballyPositioned { layoutCoordinates ->
                            pageHeaderSize = layoutCoordinates.size.height
                        },
                    playlistDetail = playlistDetail,
                    onSubTitleClicked = playlistDetailListener::onSubtitleClicked,
                    onCoverLoaded = onCoverLoaded,
                )
            }
            stickyHeader {
                PlaylistPanel(
                    modifier = Modifier
                        .padding(
                            top = panelTopPadding,
                            bottom = UiConstants.Spacing.mediumPlus,
                        ),
                    containerColor = backgroundColor,
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
                    onLongClick = { onLongSelectOnMusic(elt) },
                    onMoreClicked = { onShowMusicBottomSheet(elt) },
                    textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    isPlayedMusic = currentPlayedSong?.musicId == elt.musicId,
                    isSelected = multiSelectionState.selectedIds.contains(elt.musicId),
                    isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                )
            }
            item { SoulPlayerSpacer() }
        }
    }
}

@Composable
private fun BlurredBackground(
    cover: Cover?,
    backgroundAlpha: Float,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val windowWidth: Dp = rememberWindowWidthDp()

        SoulImage(
            roundedPercent = 0,
            cover = cover,
            size = windowWidth,
            modifier = Modifier
                .blur(radius = 10.dp)
                .blurCompat(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SoulSearchingColorTheme.colorScheme.primary.copy(
                                alpha = max(backgroundAlpha, 0.2f)
                            ),
                            SoulSearchingColorTheme.colorScheme.primary,
                        ),
                        startY = 0f,
                        endY = rememberWindowWidth(),
                    )
                ),
        )

        content()
    }
}

private const val PLAYLIST_MUSIC_CONTENT_TYPE: String = "PLAYLIST_MUSIC_CONTENT_TYPE"