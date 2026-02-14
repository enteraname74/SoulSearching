package com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.ext.blurCompat
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.ext.toPx
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.DurationIndication
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PageHeader
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistPanel
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import java.util.UUID

@Composable
fun PlaylistRowView(
    navigateBack: () -> Unit,
    searchAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    playlistDetailListener: PlaylistDetailListener,
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    optionalContent: @Composable () -> Unit = {},
) {
    var topBarHeight: Int by rememberSaveable {
        mutableIntStateOf(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SoulTopBar(
            modifier = Modifier
                .zIndex(1f)
                .onGloballyPositioned { layoutCoordinates ->
                    topBarHeight = layoutCoordinates.size.height
                },
            leftAction = TopBarNavigationAction(onClick = navigateBack),
            rightAction = object : TopBarActionSpec {
                override val icon: ImageVector = Icons.Rounded.Search
                override val onClick: () -> Unit = searchAction
            },
            colors = SoulTopBarDefaults.primary(
                containerColor = Color.Transparent,
            )
        )
        Content(
            modifier = Modifier.fillMaxSize(),
            onShowMusicBottomSheet = onShowMusicBottomSheet,
            playlistDetail = playlistDetail,
            playlistDetailListener = playlistDetailListener,
            optionalContent = optionalContent,
            onCoverLoaded = onCoverLoaded,
            onLongSelectOnMusic = onLongSelectOnMusic,
            multiSelectionState = multiSelectionState,
            topBarHeight = topBarHeight.toDp(),
        )
    }
}

@Composable
private fun Content(
    topBarHeight: Dp,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier,
    optionalContent: @Composable () -> Unit = {},
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    playbackManager: PlaybackManager = injectElement(),
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

    val musics = playlistDetail.musics.collectAsLazyPagingItems()

    Row(
        modifier = modifier,
    ) {
        BlurredBackground(
            cover = playlistDetail.cover,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topBarHeight)
            ) {
                PageHeader(
                    playlistDetail = playlistDetail,
                    onSubTitleClicked = playlistDetailListener::onSubtitleClicked,
                    onCoverLoaded = onCoverLoaded,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = topBarHeight)
        ) {
            PlaylistPanel(
                editAction = playlistDetailListener.onEdit,
                shuffleAction = playlistDetailListener::onShuffleClicked,
                playAction = playlistDetailListener::onPlayClicked,
            )
            LazyColumnCompat {
                item {
                    optionalContent()
                }

                items(
                    count = musics.itemCount,
                    key = { musics[it]?.musicId ?: UUID.randomUUID() },
                    contentType = { PLAYLIST_MUSICS_CONTENT_TYPE }
                ) { pos ->
                    val music = musics[pos]
                    music?.let {
                        MusicItemComposable(
                            modifier = Modifier
                                .animateItem(),
                            music = music,
                            onClick = playlistDetailListener::onPlayClicked,
                            onLongClick = { onLongSelectOnMusic(music) },
                            onMoreClicked = { onShowMusicBottomSheet(music) },
                            isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                            isSelected = multiSelectionState.selectedIds.contains(music.musicId),
                            isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                            leadingSpec = playlistDetail.musicItemLeadingSpec(pos)
                        )
                    }
                }
                // TODO OPTIMIZATION: Fetch duration from another way.
                item { DurationIndication(musics = emptyList()) }
                item {
                    SoulPlayerSpacer()
                }
            }
        }
    }
}

@Composable
private fun BlurredBackground(
    cover: Cover?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val width: Dp = this.maxWidth
        val height: Dp = this.maxHeight

        SoulImage(
            roundedPercent = 0,
            cover = cover,
            size = max(width, height),
            modifier = Modifier
                .blurCompat(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SoulSearchingColorTheme.colorScheme.primary.copy(0.2f),
                            SoulSearchingColorTheme.colorScheme.primary,
                        ),
                        startX = 0f,
                        endX = width.toPx(),
                    )
                ),
        )

        content()
    }
}

private const val PLAYLIST_MUSICS_CONTENT_TYPE: String = "PLAYLIST_MUSICS_CONTENT_TYPE"