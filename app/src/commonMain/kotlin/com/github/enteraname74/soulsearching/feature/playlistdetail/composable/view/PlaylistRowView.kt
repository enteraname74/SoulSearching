package com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.ext.blurCompat
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.ext.toPx
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.DurationIndication
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PageHeader
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistPanel
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@Composable
fun PlaylistRowView(
    navigateBack: () -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    playAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    playlistDetailListener: PlaylistDetailListener,
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    optionalContent: @Composable () -> Unit = {},
) {
    var topBarHeight: Int by rememberSaveable {
        mutableStateOf(0)
    }
    val topBarHeightInDp = topBarHeight.toDp()

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
            rightAction = object: TopBarActionSpec {
                override val icon: ImageVector = Icons.Rounded.Search
                override val onClick: () -> Unit = searchAction
            },
            colors = SoulTopBarDefaults.primary(
                containerColor = Color.Transparent,
            )
        )
        Content(
            modifier = Modifier.fillMaxSize(),
            shuffleAction = shuffleAction,
            playAction = playAction,
            onShowMusicBottomSheet = onShowMusicBottomSheet,
            playlistDetail = playlistDetail,
            playlistDetailListener = playlistDetailListener,
            optionalContent = optionalContent,
            onCoverLoaded = onCoverLoaded,
            onLongSelectOnMusic = onLongSelectOnMusic,
            multiSelectionState = multiSelectionState,
            topBarHeight = topBarHeightInDp,
        )
    }
}

@Composable
private fun Content(
    topBarHeight: Dp,
    shuffleAction: () -> Unit,
    playAction: () -> Unit,
    onShowMusicBottomSheet: (Music) -> Unit,
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier,
    optionalContent: @Composable () -> Unit = {},
    multiSelectionState: MultiSelectionState,
    onLongSelectOnMusic: (Music) -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
                shuffleAction = shuffleAction,
                playAction = playAction,
            )
            LazyColumnCompat {
                item {
                    optionalContent()
                }

                items(
                    count = playlistDetail.musics.size,
                    key = { playlistDetail.musics[it].musicId },
                    contentType = { PLAYLIST_MUSICS_CONTENT_TYPE }
                ) { pos ->
                    val music = playlistDetail.musics[pos]
                    MusicItemComposable(
                        modifier = Modifier
                            .animateItem(),
                        music = music,
                        onClick = {
                            playlistDetailListener.onUpdateNbPlayed()
                            coroutineScope.launch {
                                playbackManager.setCurrentPlaylistAndMusic(
                                    music = music,
                                    musicList = playlistDetail.musics,
                                    playlistId = playlistDetail.id,
                                    isMainPlaylist = false,
                                )
                                playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                            }
                        },
                        onLongClick = { onLongSelectOnMusic(music) },
                        onMoreClicked = { onShowMusicBottomSheet(music) },
                        isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                        isSelected = multiSelectionState.selectedIds.contains(music.musicId),
                        isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                        leadingSpec = playlistDetail.musicItemLeadingSpec(pos)
                    )
                }
                item { DurationIndication(musics = playlistDetail.musics) }
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