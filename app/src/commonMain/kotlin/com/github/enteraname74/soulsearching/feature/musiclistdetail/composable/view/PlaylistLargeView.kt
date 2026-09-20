package com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_edit_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_search
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_shuffle
import com.github.enteraname74.soulsearching.coreui.ext.blurCompat
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowWidthDp
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailState
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.DurationIndication
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.PlaylistContinueCard
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.PlaylistPartTitle
import com.github.enteraname74.soulsearching.feature.musiclistdetail.ext.Content
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import org.jetbrains.compose.resources.DrawableResource
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Composable
fun PlaylistLargeView(
    data: MusicListDetailState.Data,
    openSearchView: () -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    multiSelectionState: MultiSelectionState,
) {
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()
    val lazyListState = rememberLazyListState()

    val musics = data.musics.collectAsLazyPagingItems()

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
                    cover = data.cover,
                    height = height.toDp(),
                )

                Header(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            height = layoutCoordinates.size.height
                        },
                    data = data,
                    openSearchView = openSearchView,
                )
            }
        }
        data.cachedPlayedListUiSpec?.let {
            item(
                key = PLAYLIST_CONTINUE_KEY,
                contentType = PLAYLIST_CONTINUE_CONTENT_TYPE,
            ) {
                PlaylistContinueCard(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .padding(
                            start = UiConstants.Spacing.medium,
                            end = UiConstants.Spacing.medium,
                            bottom = UiConstants.Spacing.mediumPlus,
                        )
                        .animateItem(
                            // TODO IMPROVE: improve screen to find a way to use placement anim
                            placementSpec = null,
                        ),
                    spec = it,
                )
            }
        }
        data.optionalContent?.let { optionalContent ->
            item {
                optionalContent.Content(multiSelectionState)
            }
        }
        if (musics.itemCount > 0) {
            item {
                PlaylistPartTitle(title = strings.elementDetailTitles)
            }
        }
        items(
            count = musics.itemCount,
            key = { musics[it]?.musicId ?: Uuid.random() },
            contentType = { PLAYLIST_MUSIC_CONTENT_TYPE }
        ) { pos ->

            val music = musics[pos]
            music?.let {
                MusicItemComposable(
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = UiConstants.Spacing.huge),
                    music = music,
                    onClick = data.onPlay,
                    onLongClick = { data.onLongClickOnMusic(music.musicId) },
                    onMoreClicked = {
                        data.showMusicBottomSheet(music.musicId)
                    },
                    textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                    isSelected = multiSelectionState.selectedIds.contains(music.musicId.toString()),
                    isSelectionModeOn = multiSelectionState.totalSelected > 0,
                    padding = PaddingValues(
                        vertical = UiConstants.Spacing.medium,
                    ),
                    leadingSpec = data.musicItemLeadingSpec(pos)
                )
            }
        }
        if (data.duration != Duration.ZERO && musics.itemCount > 0) {
            item { DurationIndication(duration = data.duration) }
        }
        item { SoulPlayerSpacer() }
    }
}

@Composable
private fun Header(
    openSearchView: () -> Unit,
    data: MusicListDetailState.Data,
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
                onClick = data.navigateBack,
            ),
            rightAction = object : TopBarActionSpec {
                override val icon: DrawableResource = CoreRes.drawable.ic_search
                override val onClick: () -> Unit = openSearchView
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
                    cover = data.cover,
                    size = PLAYLIST_COVER_SIZE,
                    roundedPercent = 5,
                    onSuccess = data.onCoverLoaded,
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
                        text = data.type,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Text(
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        text = data.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = PLAYLIST_TITLE_SIZE,
                        lineHeight = PLAYLIST_TITLE_LINE_HEIGHT,
                    )
                    Text(
                        modifier = Modifier.optionalClickable(data.onSubtitleClicked),
                        color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                        text = data.subTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Actions(
                        modifier = Modifier
                            .padding(
                                top = UiConstants.Spacing.medium,
                            ),
                        data = data,
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
    data: MusicListDetailState.Data
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        Button(
            title = strings.elementDetailPlay,
            icon = CoreRes.drawable.ic_play_filled,
            action = { data.onPlay(null) },
            colors = SoulButtonDefaults.colors(
                contentColor = SoulSearchingColorTheme.colorScheme.secondary,
                containerColor = SoulSearchingColorTheme.colorScheme.onSecondary,
            )
        )
        Button(
            title = strings.elementDetailShuffle,
            icon = CoreRes.drawable.ic_shuffle,
            action = data.onShuffle,
        )
        data.onEdit?.let {
            Button(
                title = strings.elementDetailEdit,
                icon = CoreRes.drawable.ic_edit_filled,
                action = it,
            )
        }
    }
}

@Composable
private fun Button(
    title: String,
    icon: DrawableResource,
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
                color = colors.contentColor
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

private const val PLAYLIST_CONTINUE_KEY = "PLAYLIST_CONTINUE_KEY"
private const val PLAYLIST_CONTINUE_CONTENT_TYPE = "PLAYLIST_CONTINUE_CONTENT_TYPE"
private const val PLAYLIST_MUSIC_CONTENT_TYPE: String = "PLAYLIST_MUSIC_CONTENT_TYPE"
private val PLAYLIST_COVER_SIZE: Dp = 250.dp
private val PLAYLIST_TITLE_SIZE: TextUnit = 54.sp
private val PLAYLIST_TITLE_LINE_HEIGHT: TextUnit = 56.sp
