package com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.ext.blurCompat
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidth
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.DurationIndication
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PageHeader
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistPanel
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import java.util.UUID
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistSmallView(
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    navigateBack: () -> Unit,
    playAction: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    optionalContent: @Composable () -> Unit = {},
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()
    val lazyListState = rememberLazyListState()

    var topBarHeight: Int by rememberSaveable { mutableIntStateOf(0) }
    var pageHeaderSize: Int by rememberSaveable { mutableIntStateOf(0) }

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

    val panelTopPadding: Dp by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex >= 1) {
                topBarHeightDp
            } else if (topBarHeight == 0) {
                0.dp
            } else {
                topBarHeightDp
                    .times(lazyListState.firstVisibleItemScrollOffset.toFloat() / pageHeaderSize)
                    .coerceIn(0.dp, topBarHeightDp)
            }
        }
    }


    val backgroundColor: @Composable () -> Color by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex >= 1) {
                { SoulSearchingColorTheme.colorScheme.primary }
            } else {
                { Color.Transparent }
            }
        }
    }

    val topBarTitle: String? by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex >= 1) {
                playlistDetail.title
            } else {
                null
            }
        }
    }

    val musics = playlistDetail.musics.collectAsLazyPagingItems()

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
            title = topBarTitle,
            leftAction = TopBarNavigationAction(onClick = navigateBack),
            rightAction = object: TopBarActionSpec {
                override val icon: ImageVector = Icons.Rounded.Search
                override val onClick: () -> Unit = searchAction
            },
            colors = SoulTopBarDefaults.primary(
                containerColor = backgroundColor(),
            )
        )
        LazyColumnCompat(
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
                    containerColor = backgroundColor(),
                    editAction = playlistDetailListener.onEdit,
                    shuffleAction = {
                        playlistDetailListener.onUpdateNbPlayed()
                        shuffleAction()
                    },
                    playAction = playAction,
                )
            }
            item {
                optionalContent()
            }
            items(
                count = musics.itemCount,
                key = { musics[it]?.musicId ?: UUID.randomUUID() },
                contentType = { PLAYLIST_MUSIC_CONTENT_TYPE }
            ) { pos ->
                val music = musics[pos]
                music?.let {
                    MusicItemComposable(
                        music = music,
                        onClick = {
                            // TODO OPTIMIZATION: Move to listener
//                            playlistDetailListener.onUpdateNbPlayed()
//                            coroutineScope.launch {
//                                playbackManager.setCurrentPlaylistAndMusic(
//                                    music = music,
//                                    musicList = playlistDetail.musics,
//                                    playlistId = playlistDetail.id,
//                                    isMainPlaylist = false
//                                )
//                                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
//                            }
                        },
                        onLongClick = { onLongSelectOnMusic(music) },
                        onMoreClicked = { onShowMusicBottomSheet(music) },
                        textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                        isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                        isSelected = multiSelectionState.selectedIds.contains(music.musicId),
                        isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                        leadingSpec = playlistDetail.musicItemLeadingSpec(pos)
                    )
                }
            }
            // TODO OPTIMIZATION: Fetch duration from another way.
            item { DurationIndication(musics = emptyList()) }
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