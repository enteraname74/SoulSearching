package com.github.enteraname74.soulsearching.feature.playlistdetail.composable.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.blurCompat
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistPartTitle
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailListener
import com.github.enteraname74.soulsearching.feature.playlistdetail.ext.title
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@Composable
fun PlaylistLargeView(
    playlistDetail: PlaylistDetail,
    playlistDetailListener: PlaylistDetailListener,
    navigateBack: () -> Unit,
    playAction: () -> Unit,
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

    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState,
    ) {
        item {
            var height by rememberSaveable {
                mutableIntStateOf(0)
            }

            Box {
                BlurredBackground(
                    cover = playlistDetail.cover,
                    height = height.toDp(),
                )

                Header(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            height = layoutCoordinates.size.height
                        },
                    playlistDetail = playlistDetail,
                    onCoverLoaded = onCoverLoaded,
                    shuffleAction = shuffleAction,
                    playAction = playAction,
                    searchAction = searchAction,
                    playlistDetailListener = playlistDetailListener,
                    navigateBack = navigateBack,
                )
            }
        }
        item {
            optionalContent()
        }
        item {
            PlaylistPartTitle(title = strings.elementDetailTitles)
        }
        items(
            items = playlistDetail.musics,
            key = { it.musicId },
            contentType = { PLAYLIST_MUSIC_CONTENT_TYPE }
        ) { elt ->
            MusicItemComposable(
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = UiConstants.Spacing.huge),
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
                padding = PaddingValues(
                    vertical = UiConstants.Spacing.medium,
                )
            )
        }
        item { SoulPlayerSpacer() }
    }
}

@Composable
private fun Header(
    navigateBack: () -> Unit,
    playlistDetail: PlaylistDetail,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    shuffleAction: () -> Unit,
    searchAction: () -> Unit,
    playAction: () -> Unit,
    playlistDetailListener: PlaylistDetailListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        SoulTopBar(
            colors = SoulTopBarDefaults.primary(
                containerColor = Color.Transparent,
            ),
            leftAction = TopBarNavigationAction(
                onClick = navigateBack,
            ),
            rightAction = object: TopBarActionSpec {
                override val icon: ImageVector = Icons.Rounded.Search
                override val onClick: () -> Unit = searchAction
            }
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = UiConstants.Spacing.large,
                    bottom = UiConstants.Spacing.huge,
                    start = UiConstants.Spacing.huge,
                    end = UiConstants.Spacing.huge,
                ),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                SoulImage(
                    contentScale = ContentScale.Crop,
                    cover = playlistDetail.cover,
                    size = PLAYLIST_COVER_SIZE,
                    roundedPercent = 5,
                    onSuccess = onCoverLoaded,
                )
            }
            Box(
                contentAlignment = Alignment.TopStart,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = UiConstants.Spacing.large),
                ) {
                    Text(
                        modifier = Modifier.padding(
                            bottom = UiConstants.Spacing.medium,
                        ),
                        color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                        text = playlistDetail.type.title(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Text(
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        text = playlistDetail.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = PLAYLIST_TITLE_SIZE,
                        lineHeight = PLAYLIST_TITLE_LINE_HEIGHT,
                    )
                    playlistDetail.subTitle?.let {
                        Text(
                            modifier = Modifier.clickableWithHandCursor {
                                playlistDetailListener.onSubtitleClicked()
                            },
                            color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                    Actions(
                        modifier = Modifier
                            .padding(
                                top = UiConstants.Spacing.medium,
                            ),
                        playlistDetailListener = playlistDetailListener,
                        shuffleAction = shuffleAction,
                        playAction = playAction,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    playlistDetailListener: PlaylistDetailListener,
    shuffleAction: () -> Unit,
    playAction: () -> Unit,
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        Button(
            title = strings.elementDetailPlay,
            icon = Icons.Rounded.PlayArrow,
            action = playAction,
            colors = SoulButtonDefaults.colors(
                contentColor = SoulSearchingColorTheme.colorScheme.secondary,
                containerColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            )
        )
        Button(
            title = strings.elementDetailShuffle,
            icon = Icons.Rounded.Shuffle,
            action = shuffleAction,
        )
        playlistDetailListener.onEdit?.let {
            Button(
                title = strings.elementDetailEdit,
                icon = Icons.Rounded.Edit,
                action = it,
            )
        }
    }
}

@Composable
private fun Button(
    title: String,
    icon: ImageVector,
    action: () -> Unit,
    colors: SoulButtonColors = SoulButtonDefaults.secondaryColors(),
) {
    SoulButton(
        onClick = action,
        colors = colors,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = UiConstants.Spacing.small,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulIcon(
                icon = icon,
                tint = colors.contentColor
            )
            Text(
                text = title,
                style = UiConstants.Typography.body.copy(
                    fontWeight = FontWeight.Medium,
                    color = colors.contentColor,
                )
            )
        }
    }
}

@Composable
private fun BlurredBackground(
    cover: Cover?,
    height: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
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
                            SoulSearchingColorTheme.colorScheme.primary.copy(alpha = 0.3f),
                            SoulSearchingColorTheme.colorScheme.primary,
                        ),
                    )
                ),
        )
    }
}

private const val PLAYLIST_MUSIC_CONTENT_TYPE: String = "PLAYLIST_MUSIC_CONTENT_TYPE"
private val PLAYLIST_COVER_SIZE: Dp = 250.dp
private val PLAYLIST_TITLE_SIZE: TextUnit = 54.sp
private val PLAYLIST_TITLE_LINE_HEIGHT: TextUnit = 56.sp
